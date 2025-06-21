package com.clinic.dto;

//import jakarta.validation.constraints.*;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PatientDTO {

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

	@NotNull(message = "Date of birth is required")
	@PastOrPresent(message = "Date of birth cannot be in the future")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;

	private String gender;

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

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}
