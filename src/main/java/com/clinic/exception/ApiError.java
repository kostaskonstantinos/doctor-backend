package com.clinic.exception;

public class ApiError {
	private final String code;
	private final String message;
	private final String field; // nullable

	public ApiError() {
		this.code = null;
		this.message = null;
		this.field = null;
	}

	public ApiError(ErrorCode code, String message) {
		this(code, message, null);
	}

	public ApiError(ErrorCode code, String message, String field) {
		this.code = code.name();
		this.message = message;
		this.field = field;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getField() {
		return field;
	}
}
