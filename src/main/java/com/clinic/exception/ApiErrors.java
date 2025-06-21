package com.clinic.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import com.clinic.dto.SuccessResponse;

public class ApiErrors {

	public static Response badRequest(List<ApiError> errors) {
		return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("errors", errors)).build();
	}

	public static Response badRequest(ErrorCode code, String message) {
		return badRequest(code, message, null);
	}

	public static Response badRequest(ErrorCode code, String message, String field) {
		return badRequest(List.of(new ApiError(code, message, field)));
	}

	public static Response conflict(ErrorCode code, String message) {
		return Response.status(Response.Status.CONFLICT).entity(new ApiError(code, message)).build();
	}

	public static Response notFound(ErrorCode code, String message) {
		return Response.status(Response.Status.NOT_FOUND).entity(new ApiError(code, message)).build();
	}

	public static Response internalServerError(String message) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new ApiError(ErrorCode.SERVER_ERROR, message)).build();
	}

	public static Response created(ErrorCode code, String message) {
		return Response.status(Response.Status.CREATED).entity(new SuccessResponse(code.name(), message)).build();
	}

	public static Response ok(ErrorCode code, String message) {
		return Response.status(Response.Status.OK).entity(new SuccessResponse(code.name(), message)).build();
	}

	public static <T> Response okData(T data) {
		return Response.status(Response.Status.OK).entity(data).type(MediaType.APPLICATION_JSON).build();
	}

}
