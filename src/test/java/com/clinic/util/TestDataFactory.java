package com.clinic.util;

import com.clinic.dto.*;
import com.clinic.entity.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TestDataFactory {
	private static final LocalDateTime FIXED_SLOT_TIME = LocalDateTime.of(2026, 1, 1, 10, 0);

	public static DoctorDTO sampleDoctorDTO() {
		DoctorDTO dto = new DoctorDTO();
		dto.setEmail("doc@example.com");
		dto.setPassword("docpass123");
		dto.setName("Doc John");
		dto.setPhoneNumber("1234567890");
		dto.setAddress("Clinic Street");
		dto.setSpecialization("Dermatology");
		dto.setWorkingHours("09:00 - 15:00");
		return dto;
	}

	public static PatientDTO samplePatientDTO() {
		PatientDTO dto = new PatientDTO();
		dto.setEmail("pat@example.com");
		dto.setPassword("securePass123");
		dto.setName("John Doe");
		dto.setPhoneNumber("1234567890");
		dto.setAddress("123 Main St");
		dto.setGender("M");
		dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
		return dto;
	}

	public static Patient buildPatient(String email) {
		return new Patient.Builder().email(email).password(PasswordUtil.hashPassword("securePass123"))
				.name("Test Patient").phoneNumber("9876543210").address("456 Clinic Rd").gender("F")
				.dateOfBirth(LocalDate.of(1992, 2, 2)).build();
	}

	public static Doctor buildDoctor(String email) {
		return new Doctor.Builder().email(email).password(PasswordUtil.hashPassword("docPass123")).name("Dr. Test")
				.phoneNumber("1112223333").address("789 Medical Blvd").specialization("Cardiology").workingHours("9-5")
				.build();
	}

	public static DoctorSlot bookedSlot(Doctor doctor, Patient patient) {
		return new DoctorSlot.Builder().doctor(doctor).booked(true).bookedBy(patient).startTime(FIXED_SLOT_TIME)
				.build();
	}

	public static DoctorSlot unbookedSlot(Doctor doctor) {
		return new DoctorSlot.Builder().doctor(doctor).booked(false).startTime(FIXED_SLOT_TIME).build();
	}

	public static SlotRequest sampleSlotRequest(Long doctorId) {

		SlotRequest request = new SlotRequest();
		request.setDoctorId(doctorId);
		request.setStartTime(FIXED_SLOT_TIME); // Only startTime is needed

		return request;
	}

}
