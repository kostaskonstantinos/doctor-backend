package com.clinic.dto;

import java.time.LocalDate;

public class PatientViewDTO {

	private Long id;
	private String name;
	private String email;
	private String phoneNumber;
	private String address;
	private LocalDate dateOfBirth;
	private String gender;

	// No-arg constructor (required for some serializers)
	public PatientViewDTO() {
	}

	// All-args constructor
	public PatientViewDTO(String name, String email, String phoneNumber, String address, LocalDate dateOfBirth,
			String gender) {
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
	}

	public PatientViewDTO(Long id, String name, String email, String phoneNumber, String address, LocalDate dateOfBirth,
			String gender) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
	}

	// Getters
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	// Setters
	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}
