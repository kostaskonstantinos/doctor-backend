package com.clinic.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable exception) {

		System.err.println("Unhandled exception: " + exception.getMessage());
		exception.printStackTrace();

		ApiError error = new ApiError(ErrorCode.INTERNAL_ERROR, "An unexpected error occurred", null);

		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
	}
}
