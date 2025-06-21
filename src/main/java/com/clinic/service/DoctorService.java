package com.clinic.service;

import java.util.List;
import java.util.stream.Collectors;
import com.clinic.adapter.DoctorAdapter;
import com.clinic.dto.DoctorDTO;
import com.clinic.dto.DoctorUpdateDTO;
import com.clinic.dto.DoctorViewDTO;
import com.clinic.entity.Doctor;
import com.clinic.exception.ApiException;
import com.clinic.exception.ErrorCode;
import com.clinic.util.PasswordUtil;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;

@Stateless
public class DoctorService {

	@PersistenceContext(unitName = "ClinicPU")
	private EntityManager em;

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	@Inject
	private DoctorAdapter doctorAdapter;

	public void setDoctorAdapter(DoctorAdapter doctorAdapter) {
		this.doctorAdapter = doctorAdapter;
	}

	// HELPERS
	private boolean emailExists(String email) {
		Long count = em.createQuery("SELECT COUNT(d) FROM Doctor d WHERE d.email = :email", Long.class)
				.setParameter("email", email).getSingleResult();
		return count > 0;
	}

	public Doctor findById(Long id) {
		Doctor doctor = em.find(Doctor.class, id);
		if (doctor == null) {
			throw new ApiException(ErrorCode.DOCTOR_NOT_FOUND, "Doctor not found");
		}
		return doctor;
	}

	// CREATE DOCTORS
	@Transactional
	public void createDoctor(DoctorDTO dto) {
		if (emailExists(dto.getEmail())) {
			throw new ApiException(ErrorCode.DOCTOR_ALREADY_EXISTS, "Doctor already exists", "email");
		}
		Doctor doctor = doctorAdapter.toEntity(dto);
		em.persist(doctor);
	}

	// DELETE DOCTORS
	@Transactional
	public void deleteDoctorIfAllowed(Long id) {
		Doctor doctor = em.find(Doctor.class, id);
		if (doctor == null) {
			throw new ApiException(ErrorCode.DOCTOR_NOT_FOUND, "Doctor not found");
		}

		Long count = em.createQuery("SELECT COUNT(s) FROM DoctorSlot s WHERE s.booked = true AND s.doctor.id = :id",
				Long.class).setParameter("id", id).getSingleResult();

		if (count > 0) {
			throw new ApiException(ErrorCode.DOCTOR_HAS_BOOKINGS, "Doctor has booked appointments");
		}

		// Clean up not booked slots
		em.createQuery("DELETE FROM DoctorSlot s WHERE s.doctor.id = :id AND s.booked = false").setParameter("id", id)
				.executeUpdate();

		em.remove(doctor);
	}

	// FETCH DOCTORS
	public List<DoctorViewDTO> getAllDoctors() {
		List<Doctor> doctors = em.createQuery("SELECT d FROM Doctor d", Doctor.class).getResultList();

		return doctors.stream().map(doctorAdapter::toViewDTO).collect(Collectors.toList());
	}

	public DoctorViewDTO getDoctorByEmail(String email) {
		Doctor doctor = em.createQuery("SELECT d FROM Doctor d WHERE d.email = :email", Doctor.class)
				.setParameter("email", email).getResultStream().findFirst()
				.orElseThrow(() -> new ApiException(ErrorCode.DOCTOR_NOT_FOUND, "Doctor not found"));

		return doctorAdapter.toViewDTO(doctor);
	}

	// UPDATE DOCTORS
	@Transactional
	public void updateDoctor(Long id, DoctorUpdateDTO dto) {
		Doctor existing = em.find(Doctor.class, id);
		if (existing == null) {
			throw new ApiException(ErrorCode.DOCTOR_NOT_FOUND, "Doctor not found");
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
		if (dto.getSpecialization() != null && !dto.getSpecialization().isBlank()) {
			existing.setSpecialization(dto.getSpecialization());
		}
		if (dto.getWorkingHours() != null && !dto.getWorkingHours().isBlank()) {
			existing.setWorkingHours(dto.getWorkingHours());
		}
		if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
			String hashed = PasswordUtil.hashPassword(dto.getPassword());
			existing.setPassword(hashed);
		}

		em.merge(existing);
	}

}
