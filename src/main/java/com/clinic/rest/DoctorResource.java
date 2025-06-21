package com.clinic.rest;

import com.clinic.dto.DoctorDTO;
import com.clinic.dto.DoctorUpdateDTO;
import com.clinic.dto.DoctorViewDTO;
import com.clinic.entity.Doctor;
import com.clinic.exception.ApiErrors;
import com.clinic.exception.ErrorCode;
import com.clinic.service.DoctorService;
import com.clinic.util.AuthUtil;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/doctors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DoctorResource {
	@Context
	SecurityContext securityContext;

	@Inject
	private DoctorService doctorService;

	public void setDoctorService(DoctorService doctorService) {
		this.doctorService = doctorService;
	}

	// CREATE DOCTORS
	@POST
	public Response createDoctor(@Valid DoctorDTO dto) {
		AuthUtil.requireAdminn(securityContext);
		doctorService.createDoctor(dto);
		return ApiErrors.created(ErrorCode.DOCTOR_CREATED, "Doctor registered successfully");
	}

	// DELETE DOCTORS
	@DELETE
	@Path("/{id}")
	public Response deleteDoctor(@PathParam("id") Long id) {
		AuthUtil.requireAdminn(securityContext);
		doctorService.deleteDoctorIfAllowed(id);
		return ApiErrors.ok(ErrorCode.DOCTOR_DELETED, "Doctor deleted successfully");
	}

	// UPDATE DOCTORS
	@PUT
	@Path("/{id}")
	public Response updateDoctor(@PathParam("id") Long id, @Valid DoctorUpdateDTO dto) {
		Doctor targetDoctor = doctorService.findById(id);
		AuthUtil.requireAdminOrMatchingDoctor(securityContext, targetDoctor.getEmail());
		doctorService.updateDoctor(id, dto);
		return ApiErrors.ok(ErrorCode.DOCTOR_UPDATED, "Doctor updated successfully");
	}

	// FETCH DOCTORS
	@GET
	@Path("/all")
	public Response getAllDoctors() {
		AuthUtil.requireAdminOrPatient(securityContext);
		List<DoctorViewDTO> doctors = doctorService.getAllDoctors();
		return ApiErrors.okData(doctors);
	}

	@GET
	@Path("/me")
	public Response getCurrentDoctor() {
		String email = AuthUtil.requireEmail(securityContext);
		DoctorViewDTO dto = doctorService.getDoctorByEmail(email);
		return ApiErrors.okData(dto);
	}

}
