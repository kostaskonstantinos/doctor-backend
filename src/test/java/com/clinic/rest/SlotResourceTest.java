package com.clinic.rest;

import com.clinic.dto.SlotRequest;
import com.clinic.dto.SlotViewDTO;
import com.clinic.entity.Doctor;
import com.clinic.entity.DoctorSlot;
import com.clinic.entity.Patient;
import com.clinic.exception.ApiException;
import com.clinic.exception.ErrorCode;
import com.clinic.dto.SuccessResponse;
import com.clinic.service.DoctorService;
import com.clinic.service.PatientService;
import com.clinic.service.SlotService;
import com.clinic.util.BaseJpaTest;
import com.clinic.util.StubSecurityContext;
import com.clinic.util.TestDataFactory;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SlotResource Test")
public class SlotResourceTest extends BaseJpaTest {

	private SlotService slotService;
	private DoctorService doctorService;
	private PatientService patientService;
	private SlotResource slotResource;

	@BeforeEach
	void setup() {
		doctorService = new DoctorService();
		patientService = new PatientService();
		slotService = new SlotService();

		doctorService.setEntityManager(em);
		patientService.setEntityManager(em);
		slotService.setEntityManager(em);

		slotResource = new SlotResource();
		slotResource.setDoctorService(doctorService);
		slotResource.setPatientService(patientService);
		slotResource.setSlotService(slotService);
	}

	// ------------------------------
	@Nested
	@DisplayName("Slot Creation")
	class SlotCreation {

		@Test
		@DisplayName("Should create manual slot as matching doctor")
		void createManualSlot_shouldSucceed_forDoctor() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@clinic.com");
			em.persist(doctor);
			commit();
			begin();

			slotResource.setSecurityContext(new StubSecurityContext(false, true, false, "doc@clinic.com"));

			SlotRequest request = TestDataFactory.sampleSlotRequest(doctor.getId());
			Response response = slotResource.createManualSlot(request);

			assertEquals(201, response.getStatus());
			SuccessResponse success = (SuccessResponse) response.getEntity();
			assertEquals(ErrorCode.SLOT_CREATED.name(), success.getCode());
		}

		@Test
		@DisplayName("Should generate yearly slots for matching doctor")
		void generateYearlySlots_shouldSucceed_forDoctor() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@clinic.com");
			em.persist(doctor);
			commit();
			begin();

			slotResource.setSecurityContext(new StubSecurityContext(false, true, false, "doc@clinic.com"));

			Response response = slotResource.generateYearlySlots(doctor.getId());

			assertEquals(201, response.getStatus());
			SuccessResponse res = (SuccessResponse) response.getEntity();
			assertTrue(res.getMessage().contains("Slots created successfully"));
		}
	}

	// ------------------------------
	@Nested
	@DisplayName("Slot Deletion")
	class SlotDeletion {

		@Test
		@DisplayName("Should delete one slot as admin")
		void deleteOneSlot_shouldSucceed_forAdmin() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@clinic.com");
			DoctorSlot slot = TestDataFactory.unbookedSlot(doctor);

			em.persist(doctor);
			em.persist(slot);
			commit();
			begin();

			slotResource.setSecurityContext(new StubSecurityContext(true, false, false, "admin@clinic.com"));

			Response response = slotResource.deleteOneSlot(slot.getId());

			assertEquals(200, response.getStatus());
		}
	}

	// ------------------------------
	@Nested
	@DisplayName("Slot Booking and Canceling")
	class SlotBookingCanceling {

		@Test
		@DisplayName("Should book slot for patient")
		void bookSlot_shouldSucceed_forPatient() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@clinic.com");
			Patient patient = TestDataFactory.buildPatient("pat@clinic.com");
			DoctorSlot slot = TestDataFactory.unbookedSlot(doctor);

			em.persist(doctor);
			em.persist(patient);
			em.persist(slot);
			commit();
			begin();

			slotResource.setSecurityContext(new StubSecurityContext(false, false, true, "pat@clinic.com"));

			Response response = slotResource.bookSlot(slot.getId(), patient.getId());

			assertEquals(200, response.getStatus());
			SuccessResponse res = (SuccessResponse) response.getEntity();
			assertEquals(ErrorCode.SLOT_BOOKED.name(), res.getCode());
		}

		@Test
		@DisplayName("Should cancel booked slot as matching patient")
		void cancelSlot_shouldSucceed_forPatient() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@clinic.com");
			Patient patient = TestDataFactory.buildPatient("pat@clinic.com");
			DoctorSlot slot = TestDataFactory.bookedSlot(doctor, patient);

			em.persist(doctor);
			em.persist(patient);
			em.persist(slot);
			commit();
			begin();

			slotResource.setSecurityContext(new StubSecurityContext(false, false, true, "pat@clinic.com"));

			Response response = slotResource.cancelSlot(slot.getId());

			assertEquals(200, response.getStatus());
			SuccessResponse res = (SuccessResponse) response.getEntity();
			assertEquals(ErrorCode.SLOT_CANCELED.name(), res.getCode());
		}

		@Test
		@DisplayName("Should fail to cancel nonexistent slot")
		void cancelSlot_shouldFail_whenSlotNotFound() {
			slotResource.setSecurityContext(new StubSecurityContext(true, false, false, "admin@clinic.com"));

			ApiException ex = assertThrows(ApiException.class, () -> slotResource.cancelSlot(999L));

			assertEquals(ErrorCode.SLOT_NOT_FOUND, ex.getCode());
		}
	}

	// ------------------------------
	@Nested
	@DisplayName("Slot Fetching")
	class SlotFetching {

		@Test
		@DisplayName("Should return booked slots for authenticated patient")
		void getMyBookedSlots_shouldReturnList_forPatient() {
			Patient patient = TestDataFactory.buildPatient("pat@clinic.com");
			em.persist(patient);
			commit();
			begin();

			slotResource.setSecurityContext(new StubSecurityContext(false, false, true, "pat@clinic.com"));

			Response response = slotResource.getMyBookedSlots();

			assertEquals(200, response.getStatus());
			assertTrue(response.getEntity() instanceof List<?>);
		}

		@Test
		@DisplayName("Should return own slots for authenticated doctor")
		void getMySlots_shouldReturnSlots_forDoctor() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@clinic.com");
			DoctorSlot slot = TestDataFactory.unbookedSlot(doctor);

			em.persist(doctor);
			em.persist(slot);
			commit();
			begin();

			slotResource.setSecurityContext(new StubSecurityContext(false, true, false, "doc@clinic.com"));

			List<SlotViewDTO> result = slotResource.getMySlots();

			assertNotNull(result);
			assertFalse(result.isEmpty());
		}

		@Test
		@DisplayName("Should return available slots for patient")
		void getAvailableSlots_shouldSucceed_forPatient() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@clinic.com");
			em.persist(doctor);
			commit();
			begin();

			slotResource.setSecurityContext(new StubSecurityContext(false, false, true, "pat@clinic.com"));

			Response response = slotResource.getAvailableSlots(doctor.getId());

			assertEquals(200, response.getStatus());
			assertTrue(response.getEntity() instanceof List<?>);
		}
	}
}
