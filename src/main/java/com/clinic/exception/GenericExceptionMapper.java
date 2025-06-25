package com.clinic.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {//<Throwable> "father" of all exceptions

	@Override
	public Response toResponse(Throwable exception) {
       //debug console log
		System.err.println("Unhandled exception: " + exception.getMessage());
		exception.printStackTrace();

		return ApiErrors.internalServerError("An unexpected error occurred");
	}
}
