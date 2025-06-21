package com.clinic.exception;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class IllegalArgumentMapper implements ExceptionMapper<IllegalArgumentException> {
	@Override
	public Response toResponse(IllegalArgumentException e) {
		return ApiErrors.badRequest(ErrorCode.BAD_REQUEST, e.getMessage());
	}
}
