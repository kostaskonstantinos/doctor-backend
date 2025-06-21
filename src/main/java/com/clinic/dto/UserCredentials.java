package com.clinic.dto;

import jakarta.validation.constraints.NotBlank;

public class UserCredentials {
	@NotBlank(message = "{email.required}")
	private String email;
	@NotBlank(message = "{password.required}")
	private String password;

	public UserCredentials() {
	}

	public UserCredentials(String email, String password) {
		this.email = email;
		this.password = password;
	}

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
}
