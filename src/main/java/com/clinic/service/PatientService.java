package com.clinic.service;


import com.clinic.adapter.PatientAdapter;
import com.clinic.dto.PatientDTO;
import com.clinic.dto.PatientUpdateDTO;
import com.clinic.dto.PatientViewDTO;
import com.clinic.entity.Patient;
import com.clinic.exception.ApiException;
import com.clinic.exception.ErrorCode;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;

@Stateless
public class PatientService {
    @PersistenceContext(unitName = "ClinicPU")
    private EntityManager em;
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }   
    @Inject
    private PatientAdapter patientAdapter;
    public void setPatientAdapter(PatientAdapter patientAdapter) {
        this.patientAdapter = patientAdapter;
    }   
	// HELPERS
          public boolean emailExists(String email) {
        Long count = em.createQuery("SELECT COUNT(p) FROM Patient p WHERE p.email = :email", Long.class)
                       .setParameter("email", email)
                       .getSingleResult();
        return count > 0;
    }
          
        public Patient findById(Long id) {
        	Patient patient = em.find(Patient.class, id);
            if (patient == null) {
                throw new ApiException(ErrorCode.PATIENT_NOT_FOUND, "Patient not found");
            }
        return patient;
    }
        
        public String getPatientEmailById(Long patientId) {
            Patient patient = em.find(Patient.class, patientId);
            if (patient == null) {
                throw new IllegalArgumentException("Patient not found with id: " + patientId);
            }
            return patient.getEmail();
        }
    // CREATE PATIENTS
    @Transactional
      public void registerPatient(PatientDTO dto) {        
        if (emailExists(dto.getEmail())) {
            throw new ApiException(ErrorCode.PATIENT_ALREADY_EXISTS, "Email already registered", "email");
        }
        if (dto.getDateOfBirth() != null && dto.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new ApiException(ErrorCode.INVALID_DOB, "Date of birth cannot be in the future", "dateOfBirth");
        }        
        Patient patient = patientAdapter.toEntity(dto);       
        em.persist(patient); 
    }      
	// DELETE PATIENTS    
     @Transactional
    public void deletePatientIfAllowed(Long id) {
        Patient patient = em.find(Patient.class, id);
        if (patient == null) {
            throw new ApiException(ErrorCode.PATIENT_NOT_FOUND, "Patient not found");
        }

        Long count = em.createQuery(
            "SELECT COUNT(s) FROM DoctorSlot s WHERE s.booked = true AND s.bookedBy.id = :id", Long.class)
            .setParameter("id", id)
            .getSingleResult();

        if (count > 0) {
            throw new ApiException(ErrorCode.PATIENT_HAS_BOOKINGS, "Patient has booked appointments");
        }

        em.remove(patient);
    }

    
    
 	// FETCH PATIENTS   
    public List<PatientViewDTO> getAllPatients() {
        List<Patient> patients = em.createQuery("SELECT p FROM Patient p", Patient.class).getResultList();

        return patients.stream()
            .map(patientAdapter::toViewDTO)
            .collect(Collectors.toList());
    } 

 
    public PatientViewDTO getByEmail(String email) {
        Patient patient = em.createQuery("SELECT p FROM Patient p WHERE p.email = :email", Patient.class)
            .setParameter("email", email)
            .getResultStream()
            .findFirst()
            .orElseThrow(() -> new ApiException(ErrorCode.PATIENT_NOT_FOUND, "Patient not found"));  

        return patientAdapter.toViewDTO(patient);
    }

    
	// UPDATE PATIENTS   
    @Transactional
    public void updatePatient(Long id, PatientUpdateDTO dto) {
        Patient existing = findById(id);
        if (existing == null) {
            throw new ApiException(ErrorCode.PATIENT_NOT_FOUND, "Patient not found");
        }

        // Update only non-null fields
        if (dto.getName() != null && !dto.getName().isBlank()) {
            existing.setName(dto.getName());
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
            existing.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getAddress() != null && !dto.getAddress().isBlank()) {
            existing.setAddress(dto.getAddress());
        }
        if (dto.getDateOfBirth() != null) {
            if (dto.getDateOfBirth().isAfter(LocalDate.now())) {
                // Custom exception for invalid date of birth
                throw new ApiException(ErrorCode.INVALID_DOB, "Date of birth cannot be in the future.");
            }
            existing.setDateOfBirth(dto.getDateOfBirth());
        }
        if (dto.getGender() != null && !dto.getGender().isBlank()) {
            existing.setGender(dto.getGender());
        }      
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            String hashedPassword = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
            existing.setPassword(hashedPassword);
        }

        em.persist(existing); 
    }




}
