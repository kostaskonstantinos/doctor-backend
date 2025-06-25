package com.clinic.adapter;

import com.clinic.dto.PatientDTO;
import com.clinic.dto.PatientViewDTO;
import com.clinic.entity.Patient;
import com.clinic.util.PasswordUtil;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PatientAdapter {

	public Patient toEntity(PatientDTO dto) {
		return new Patient.Builder().email(dto.getEmail()).password(PasswordUtil.hashPassword(dto.getPassword()))
				.name(dto.getName()).phoneNumber(dto.getPhoneNumber()).address(dto.getAddress())
				.dateOfBirth(dto.getDateOfBirth()).gender(dto.getGender())

				.build();
	}

	public PatientDTO toDTO(Patient patient) {
		PatientDTO dto = new PatientDTO();
		dto.setEmail(patient.getEmail());
		// Do NOT include password unless absolutely needed
		dto.setName(patient.getName());
		dto.setPhoneNumber(patient.getPhoneNumber());
		dto.setAddress(patient.getAddress());
		dto.setDateOfBirth(patient.getDateOfBirth());
		dto.setGender(patient.getGender());
		return dto;
	}

	public PatientViewDTO toViewDTO(Patient patient) {
		return new PatientViewDTO(patient.getId(), patient.getName(), patient.getEmail(), patient.getPhoneNumber(),
				patient.getAddress(), patient.getDateOfBirth(), patient.getGender());
	}

//	public PatientViewDTO toViewDTOWithoutId(Patient patient) {
//		return new PatientViewDTO(patient.getName(), patient.getEmail(), patient.getPhoneNumber(), patient.getAddress(),
//				patient.getDateOfBirth(), patient.getGender());
//	}
}
