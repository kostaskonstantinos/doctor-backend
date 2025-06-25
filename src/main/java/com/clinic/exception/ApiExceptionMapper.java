package com.clinic.exception;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {

	@Override
	public Response toResponse(ApiException ex) {
		ErrorCode code = ex.getCode();
		String message = ex.getMessage();
		String field = ex.getField();
		Response.Status status;

		if (code == null) {
			System.err.println("ApiException received with null error code.");
			return ApiErrors.internalServerError("Unknown server error");
		}

		try {
			status = code.toHttpStatus();
		} catch (Exception e) {
			System.err.println("Failed to map ErrorCode to status: " + code + " → " + e.getMessage());
			return ApiErrors.internalServerError("Unexpected error while mapping exception");
		}

		ApiError error = new ApiError(code, message, field);

		return Response.status(status).entity(error).type(MediaType.APPLICATION_JSON).build();
	}
}
