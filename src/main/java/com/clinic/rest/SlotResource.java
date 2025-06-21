package com.clinic.rest;

import com.clinic.dto.SlotRequest;
import com.clinic.dto.SlotViewDTO;
import com.clinic.entity.Doctor;
import com.clinic.entity.DoctorSlot;
import com.clinic.exception.ApiErrors;
import com.clinic.exception.ApiException;
import com.clinic.exception.ErrorCode;
import com.clinic.service.DoctorService;
import com.clinic.service.PatientService;
import com.clinic.service.SlotService;
import com.clinic.util.AuthUtil;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;

@Path("/slots")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SlotResource {

	@Inject
	private SlotService slotService;
	@Inject
	private DoctorService doctorService;
	@Inject
	private PatientService patientService;

	public void setPatientService(PatientService patientService) {
		this.patientService = patientService;
	}

	public void setSlotService(SlotService slotService) {
		this.slotService = slotService;
	}

	public void setDoctorService(DoctorService doctorService) {
		this.doctorService = doctorService;
	}

	@Context
	private SecurityContext securityContext;

	public void setSecurityContext(SecurityContext securityContext) {
		this.securityContext = securityContext;
	}

	// CREATE ONE SLOT
	@POST
	@Path("/add")
	public Response createManualSlot(@Valid SlotRequest request) {
		Doctor doctor = doctorService.findById(request.getDoctorId());
		AuthUtil.requireAdminOrMatchingDoctor(securityContext, doctor.getEmail());
		slotService.createManualSlot(request);
		return ApiErrors.created(ErrorCode.SLOT_CREATED, "Slot created successfully");

	}

	// DELETE ONE SLOT
	@DELETE
	@Path("/{slotId}")
	public Response deleteOneSlot(@PathParam("slotId") Long slotId) {
		Doctor doctor = slotService.getDoctorOfSlot(slotId);
		AuthUtil.requireAdminOrMatchingDoctor(securityContext, doctor.getEmail());
		slotService.deleteSlotById(slotId);
		return ApiErrors.ok(ErrorCode.SLOT_DELETED, "Slot deleted successfully: ");
	}

	// CREATE ALL SLOTS
	@POST
	@Path("/generate/{doctorId}")
	public Response generateYearlySlots(@PathParam("doctorId") Long doctorId) {
		Doctor doctor = doctorService.findById(doctorId);
		AuthUtil.requireAdminOrMatchingDoctor(securityContext, doctor.getEmail());
		int created = slotService.generateYearlySlots(doctorId);
		return ApiErrors.created(ErrorCode.SLOT_CREATED, "Slots created successfully: " + created);

	}

	// DELETE ALL SLOTS
	@DELETE
	@Path("/delete/{doctorId}")
	public Response deleteSlots(@PathParam("doctorId") Long doctorId) {
		Doctor doctor = doctorService.findById(doctorId);
		AuthUtil.requireAdminOrMatchingDoctor(securityContext, doctor.getEmail());
		int deleted = slotService.deleteSlotsByDoctor(doctorId);
		return ApiErrors.ok(ErrorCode.SLOT_DELETED, "Slots deleted successfully: " + deleted);
	}

	// SLOT FETCHING
	@GET
	@Path("/doctor/{doctorId}/all")
	public Response getAllSlotsForDoctor(@PathParam("doctorId") Long doctorId) {
		AuthUtil.requireAdminn(securityContext);
		List<SlotViewDTO> slots = slotService.getSlotsForDoctor(doctorId);
		return Response.ok(slots).build();
	}

	@GET
	@Path("/booked")
	public Response getMyBookedSlots() {
		String email = AuthUtil.requireEmail(securityContext);
		return Response.ok(slotService.getBookedSlotsForPatient(email)).build();
	}

	@GET
	@Path("/mine")
	public List<SlotViewDTO> getMySlots() {
		String email = AuthUtil.requireEmail(securityContext);
		return slotService.getMySlots(email); // Assuming it filters by email
	}

	@GET
	@Path("/doctor/{doctorId}")
	public Response getAvailableSlots(@PathParam("doctorId") Long doctorId) {
		AuthUtil.requireAdminOrPatient(securityContext);
		return Response.ok(slotService.getAvailableSlots(doctorId)).build();
	}

	// SLOT BOOKING
	@POST
	@Path("/{slotId}/book")
	public Response bookSlot(@PathParam("slotId") Long slotId, @QueryParam("patientId") Long patientId) {
		DoctorSlot slot = slotService.findById(slotId);
		String slotDoctorEmail = slot.getDoctor().getEmail();
		String email = patientService.getPatientEmailById(patientId);
		AuthUtil.requirePatientDoctorOrAdmin(securityContext, slotDoctorEmail, email);
		slotService.bookSlot(slotId, patientId);
		return ApiErrors.ok(ErrorCode.SLOT_BOOKED, "Slot booked successfully");
	}

	// SLOT CANCELING
	@PUT
	@Path("/{slotId}/cancel")
	public Response cancelSlot(@PathParam("slotId") Long slotId) {
		DoctorSlot slot = slotService.findById(slotId);
		if (slot == null) {
			throw new ApiException(ErrorCode.SLOT_NOT_FOUND, "Slot not found");
		}

		String slotDoctorEmail = slot.getDoctor().getEmail();
		String patientEmail = slot.getBookedBy() != null ? slot.getBookedBy().getEmail() : null;
		AuthUtil.requirePatientDoctorOrAdmin(securityContext, slotDoctorEmail, patientEmail);

		slotService.cancelSlot(slotId);
		return ApiErrors.ok(ErrorCode.SLOT_CANCELED, "Slot canceled successfully");
	}
}
