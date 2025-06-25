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
	
		return ApiErrors.conflict(ErrorCode.DUPLICATE_ENTITY, "Entity already exists");

	}
}