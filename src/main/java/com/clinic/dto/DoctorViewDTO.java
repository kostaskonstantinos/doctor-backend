package com.clinic.dto;

public class DoctorViewDTO {
	private Long id;
	private String name;
	private String email;
	private String specialization;
	private String phoneNumber;
	private String address;
	private String workingHours;

	public DoctorViewDTO(Long id, String name, String email, String specialization, String phoneNumber, String address,
			String workingHours) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.specialization = specialization;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.workingHours = workingHours;
	}

	public DoctorViewDTO() {

	}

	public DoctorViewDTO(String name, String specialization, String email, String phoneNumber, String address,
			String workingHours) {
		this.name = name;
		this.specialization = specialization;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.workingHours = workingHours;
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

	public String getSpecialization() {
		return specialization;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public String getWorkingHours() {
		return workingHours;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}

}
