package com.clinic.rest;

import com.clinic.entity.Patient;
import com.clinic.exception.ApiErrors;
import com.clinic.exception.ErrorCode;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import com.clinic.dto.PatientDTO;
import com.clinic.dto.PatientUpdateDTO;
import com.clinic.dto.PatientViewDTO;
import com.clinic.util.AuthUtil;
import java.util.List;
import com.clinic.service.PatientService;

import jakarta.inject.Inject;

@Path("/patients")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PatientResource {

	@Inject
	private PatientService patientService;

	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	@Context
	SecurityContext securityContext;

	// CREATE PATIENTS
	@POST
	@Path("/register")
	public Response createPatient(@Valid PatientDTO dto) {
		patientService.registerPatient(dto);
		return ApiErrors.created(ErrorCode.PATIENT_CREATED, "Patient registered successfully");
	}

	// DELETE PATIENTS
	@DELETE
	@Path("/{id}")
	public Response deletePatient(@PathParam("id") Long id) {
		AuthUtil.requireAdminn(securityContext);
		patientService.deletePatientIfAllowed(id);
		return ApiErrors.ok(ErrorCode.PATIENT_DELETED, "Patient deleted successfully");
	}

	// UPDATE PATIENTS
	@PUT
	@Path("/{id}")
	public Response updatePatient(@PathParam("id") Long id, @Valid PatientUpdateDTO dto) {
		Patient targetPatient = patientService.findById(id);
		AuthUtil.requireAdminOrMatchingPatient(securityContext, targetPatient.getEmail());
		patientService.updatePatient(id, dto);
		return ApiErrors.ok(ErrorCode.PATIENT_UPDATED, "Patient updated successfully");
	}

	// FETCH PATIENTS
	@GET
	@Path("/all")
	public Response getAllPatients() {
		AuthUtil.requireAdminOrDoctor(securityContext);
		List<PatientViewDTO> patients = patientService.getAllPatients();
		return ApiErrors.okData(patients);
	}

	@GET
	@Path("/me")
	public Response getCurrentPatient() {
		String email = AuthUtil.requireEmail(securityContext);
		PatientViewDTO dto = patientService.getByEmail(email);
		return ApiErrors.okData(dto);
	}

}
