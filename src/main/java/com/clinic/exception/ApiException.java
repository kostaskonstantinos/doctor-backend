package com.clinic.exception;

public class ApiException extends RuntimeException {
	private final ErrorCode code;
	private final String field; // optional

	public ApiException(ErrorCode code, String message) {
		this(code, message, null);
	}

	public ApiException(ErrorCode code, String message, String field) {
		super(message);
		this.code = code;
		this.field = field;
	}

	public ErrorCode getCode() {
		return code;
	}

	public String getField() {
		return field;
	}
}
