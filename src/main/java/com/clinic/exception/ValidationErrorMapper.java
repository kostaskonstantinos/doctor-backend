package com.clinic.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ValidationErrorMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(ConstraintViolationException ex) {
		List<ApiError> errors = ex.getConstraintViolations().stream()
				.map(v -> new ApiError(ErrorCode.VALIDATION_ERROR, v.getMessage(), v.getPropertyPath().toString()))
				.collect(Collectors.toList());

		return ApiErrors.badRequest(errors);
	}
}
