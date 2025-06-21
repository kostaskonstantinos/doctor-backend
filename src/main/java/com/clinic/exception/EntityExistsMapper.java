package com.clinic.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class EntityExistsMapper implements ExceptionMapper<EntityExistsException> {

	@Override
	public Response toResponse(EntityExistsException exception) {
		ApiError error = new ApiError(ErrorCode.DUPLICATE_ENTITY, "Entity already exists", null);

		return Response.status(Response.Status.CONFLICT).entity(error).build();
	}
}