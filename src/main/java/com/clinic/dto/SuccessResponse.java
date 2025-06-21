package com.clinic.dto;

public class SuccessResponse {
	private String code;
	private String message;

	public SuccessResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
