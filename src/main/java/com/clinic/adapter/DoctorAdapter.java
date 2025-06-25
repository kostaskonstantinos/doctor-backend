package com.clinic.adapter;

import com.clinic.dto.DoctorDTO;
import com.clinic.dto.DoctorViewDTO;
import com.clinic.entity.Doctor;
import com.clinic.util.PasswordUtil;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DoctorAdapter {

	// hashes the password
	public Doctor toEntity(DoctorDTO dto) {
		return new Doctor.Builder().name(dto.getName()).specialization(dto.getSpecialization()).email(dto.getEmail())
				.password(PasswordUtil.hashPassword(dto.getPassword())).phoneNumber(dto.getPhoneNumber())
				.address(dto.getAddress()).workingHours(dto.getWorkingHours()).build();
	}

	public DoctorDTO toDTO(Doctor doctor) {
		DoctorDTO dto = new DoctorDTO();
		dto.setName(doctor.getName());
		dto.setSpecialization(doctor.getSpecialization());
		dto.setEmail(doctor.getEmail());
		// Consider omitting password in responses unless absolutely needed
		dto.setPhoneNumber(doctor.getPhoneNumber());
		dto.setAddress(doctor.getAddress());
		dto.setWorkingHours(doctor.getWorkingHours());
		return dto;
	}

	public DoctorViewDTO toViewDTO(Doctor doctor) {
		return new DoctorViewDTO(doctor.getId(), doctor.getName(), doctor.getEmail(), doctor.getSpecialization(),
				doctor.getPhoneNumber(), doctor.getAddress(), doctor.getWorkingHours());
	}

//	public DoctorViewDTO toViewDTOWOId(Doctor doctor) {
//		return new DoctorViewDTO(doctor.getName(), doctor.getEmail(), doctor.getSpecialization(),
//				doctor.getPhoneNumber(), doctor.getAddress(), doctor.getWorkingHours());
//
//	}
}
