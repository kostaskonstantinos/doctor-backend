package com.clinic.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DoctorUpdateDTO {

	@Size(min = 6, message = "{password.tooShort}")
	private String password;

	@Pattern(regexp = "[A-Za-z\\s]{2,50}", message = "{name.invalid}")
	private String name;

	@Pattern(regexp = "\\d{10,15}", message = "{phone.invalid}")
	private String phoneNumber;

	private String address;

	private String specialization;

	private String workingHours;

	// Getters and Setters

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}
}
