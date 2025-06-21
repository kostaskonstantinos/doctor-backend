package com.clinic.dto;

import jakarta.validation.constraints.*;

public class DoctorDTO {

	@NotBlank(message = "{email.required}")
	@Email(message = "{email.invalid}")
	private String email;

	@NotBlank(message = "{password.required}")
	@Size(min = 6, message = "{password.tooShort}")
	private String password;

	@NotBlank(message = "{name.required}")
	@Pattern(regexp = "[A-Za-z\\s]{2,50}", message = "{name.invalid}")
	private String name;

	@NotBlank(message = "{phone.required}")
	@Pattern(regexp = "\\d{10,15}", message = "{phone.invalid}")
	private String phoneNumber;

	private String address;
	private String specialization;
	private String workingHours;

	// Getters and setters

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

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
