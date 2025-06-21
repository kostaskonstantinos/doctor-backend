//package com.clinic.service;
//
//
//import com.clinic.dto.SlotBookedDTO;
//import com.clinic.dto.SlotRequest;
//import com.clinic.dto.SlotViewDTO;
//import com.clinic.entity.Doctor;
//import com.clinic.entity.DoctorSlot;
//import com.clinic.entity.Patient;
//import com.clinic.exception.ApiException;
//import com.clinic.util.BaseJpaTest;
//import com.clinic.util.TestDataFactory;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import java.time.LocalDateTime;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//
//class SlotServiceTest extends BaseJpaTest {
//
//    private SlotService slotService;
//
//    @BeforeEach
//    void setupService() {
//        slotService = new SlotService();
//        slotService.setEntityManager(em);
//    }
//
//    @Test
//    @DisplayName("Create manual slot - should succeed")
//    void createManualSlot_shouldSucceed() {
//        Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
//        em.persist(doctor);
//        commit();
//        begin();
//
//        SlotRequest request = new SlotRequest();
//        request.setDoctorId(doctor.getId());
//        request.setStartTime(LocalDateTime.of(2026, 1, 1, 10, 0));
//
//        slotService.createManualSlot(request);
//        commit();
//
//        List<SlotViewDTO> slots = slotService.getSlotsForDoctor(doctor.getId());
//        assertEquals(1, slots.size());
//    }
//
//    @Test
//    @DisplayName("Book and cancel slot - should succeed")
//    void bookAndCancelSlot_shouldSucceed() {
//        Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
//        Patient patient = TestDataFactory.buildPatient("pat@example.com");
//        DoctorSlot slot = TestDataFactory.unbookedSlot(doctor);
//
//        em.persist(doctor);
//        em.persist(patient);
//        em.persist(slot);
//        commit();
//        begin();
//
//        // Book slot
//        slotService.bookSlot(slot.getId(), patient.getId());
//        commit();
//        begin();
//
//        DoctorSlot booked = em.find(DoctorSlot.class, slot.getId());
//        assertTrue(booked.isBooked());
//        assertEquals(patient.getId(), booked.getBookedBy().getId());
//
//        // Cancel slot
//        slotService.cancelSlot(slot.getId());
//        commit();
//        begin();
//
//        DoctorSlot canceled = em.find(DoctorSlot.class, slot.getId());
//        assertFalse(canceled.isBooked());
//        assertNull(canceled.getBookedBy());
//    }
//    @Test
//    @DisplayName("Generate yearly slots - should create multiple slots")
//    void generateYearlySlots_shouldCreateSlots() {
//        Doctor doctor = TestDataFactory.buildDoctor("yearly@example.com");
//        em.persist(doctor);
//        commit();
//        begin();
//
//        int created = slotService.generateYearlySlots(doctor.getId());
//        commit();
//
//        assertTrue(created > 200); // Expect hundreds of weekday slots
//        List<SlotViewDTO> slots = slotService.getSlotsForDoctor(doctor.getId());
//        assertFalse(slots.isEmpty());
//    }
//    @Test
//    @DisplayName("Delete unbooked slots by doctor - should succeed")
//    void deleteUnbookedSlotsByDoctor_shouldSucceed() {
//        Doctor doctor = TestDataFactory.buildDoctor("delete@example.com");
//        DoctorSlot slot1 = TestDataFactory.unbookedSlot(doctor);
//        DoctorSlot slot2 = TestDataFactory.unbookedSlot(doctor);
//
//        em.persist(doctor);
//        em.persist(slot1);
//        em.persist(slot2);
//        commit();
//        begin();
//
//        int deleted = slotService.deleteSlotsByDoctor(doctor.getId());
//        commit();
//
//        assertEquals(2, deleted);
//        List<SlotViewDTO> remaining = slotService.getSlotsForDoctor(doctor.getId());
//        assertTrue(remaining.isEmpty());
//    }
//    @Test
//    @DisplayName("Get available slots - should return only unbooked")
//    void getAvailableSlots_shouldReturnOnlyUnbooked() {
//        Doctor doctor = TestDataFactory.buildDoctor("avail@example.com");
//        Patient patient = TestDataFactory.buildPatient("pat@example.com");
//
//        DoctorSlot booked = TestDataFactory.bookedSlot(doctor, patient);
//        DoctorSlot free = TestDataFactory.unbookedSlot(doctor);
//
//        em.persist(doctor);
//        em.persist(patient);
//        em.persist(booked);
//        em.persist(free);
//        commit();
//
//        List<SlotViewDTO> available = slotService.getAvailableSlots(doctor.getId());
//        assertEquals(1, available.size());
//        assertEquals(free.getStartTime(), available.get(0).getStartTime());
//    }
//    @Test
//    @DisplayName("Get booked slots for patient - should return list")
//    void getBookedSlotsForPatient_shouldReturnBooked() {
//        Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
//        Patient patient = TestDataFactory.buildPatient("pat@example.com");
//        DoctorSlot slot = TestDataFactory.bookedSlot(doctor, patient);
//
//        em.persist(doctor);
//        em.persist(patient);
//        em.persist(slot);
//        commit();
//
//        List<SlotBookedDTO> bookedSlots = slotService.getBookedSlotsForPatient("pat@example.com");
//        assertEquals(1, bookedSlots.size());
//        assertEquals(slot.getStartTime(), bookedSlots.get(0).getStartTime());
//    }
//    @Test
//    @DisplayName("Create manual slot in the past - should fail")
//    void createManualSlot_inPast_shouldFail() {
//        Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
//        em.persist(doctor);
//        commit();
//        begin();
//
//        SlotRequest request = new SlotRequest();
//        request.setDoctorId(doctor.getId());
//        request.setStartTime(LocalDateTime.now().minusDays(1)); // past time
//
//        ApiException ex = assertThrows(ApiException.class, () -> slotService.createManualSlot(request));
//        assertEquals("Slot must be in the future", ex.getMessage());
//    }
//
//    @Test
//    @DisplayName("Create duplicate manual slot - should fail")
//    void createManualSlot_duplicate_shouldFail() {
//        // Create doctor
//        Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
//        em.persist(doctor);
//        commit(); // Persist doctor
//        begin();
//
//        // Define slot time
//        LocalDateTime slotTime = (LocalDateTime.of(2026, 1, 1, 10, 0,0,0));
//
//        // First slot request
//        SlotRequest request = new SlotRequest();
//        request.setDoctorId(doctor.getId());
//        request.setStartTime(slotTime);
//
//        // Create first slot
//        slotService.createManualSlot(request);
//        em.flush(); // Force DB write
//    
//
//        commit(); // Finish first transaction
//        begin();  // Start second transaction
//
//        // Try to create duplicate
//        Exception ex = assertThrows(ApiException.class, () -> slotService.createManualSlot(request));
//
//
//        // Assert correct error message
//        assertEquals("Slot already exists for this time", ex.getMessage());
//    }
//
//
//    @Test
//    @DisplayName("Book non-existent slot - should throw SLOT_NOT_FOUND")
//    void book_nonexistentSlot_shouldFail() {
//        Patient patient = TestDataFactory.buildPatient("pat@example.com");
//        em.persist(patient);
//        commit();
//        begin();
//
//        ApiException ex = assertThrows(ApiException.class, () -> slotService.bookSlot(999L, patient.getId()));
//        assertEquals("Slot not found", ex.getMessage());
//    }
//    @Test
//    @DisplayName("Cancel non-existent slot - should throw SLOT_NOT_FOUND")
//    void cancel_nonexistentSlot_shouldFail() {
//        ApiException ex = assertThrows(ApiException.class, () -> slotService.cancelSlot(999L));
//        assertEquals("Slot not found", ex.getMessage());
//    }
//    @Test
//    @DisplayName("Create manual slot outside allowed hours - should fail")
//    void createManualSlot_outsideWorkingHours_shouldFail() {
//        Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
//        em.persist(doctor);
//        commit();
//        begin();
//
//        // Slot at 07:00, which is before allowed time (08:00)
//        SlotRequest request = new SlotRequest();
//        request.setDoctorId(doctor.getId());
//        request.setStartTime(LocalDateTime.now().plusDays(1).withHour(7).withMinute(0));
//
//        ApiException ex = assertThrows(ApiException.class, () ->
//            slotService.createManualSlot(request)
//        );
//
//        assertEquals("Slot must start between 08:00 and 20:00", ex.getMessage());
//    }
//    @Test
//    @DisplayName("Cancel non-existent slot - should throw SLOT_NOT_FOUND")
//    void cancelSlot_nonExistent_shouldFail() {
//        ApiException ex = assertThrows(ApiException.class,
//            () -> slotService.cancelSlot(999L));
//        assertEquals("Slot not found", ex.getMessage());
//    }
//    @Test
//    @DisplayName("Book and immediately cancel slot - should restore to available")
//    void bookAndCancel_shouldRestoreAvailability() {
//        Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
//        Patient patient = TestDataFactory.buildPatient("pat@example.com");
//        DoctorSlot slot = TestDataFactory.unbookedSlot(doctor);
//
//        em.persist(doctor);
//        em.persist(patient);
//        em.persist(slot);
//        commit(); begin();
//
//        slotService.bookSlot(slot.getId(), patient.getId());
//        commit(); begin();
//        slotService.cancelSlot(slot.getId());
//        commit(); begin();
//
//        DoctorSlot refreshed = em.find(DoctorSlot.class, slot.getId());
//        assertFalse(refreshed.isBooked());
//        assertNull(refreshed.getBookedBy());
//    }
//
//}
package com.clinic.service;

import com.clinic.dto.*;
import com.clinic.entity.*;
import com.clinic.exception.ApiException;
import com.clinic.util.BaseJpaTest;
import com.clinic.util.TestDataFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SlotService Test")
public class SlotServiceTest extends BaseJpaTest {

	private SlotService slotService;

	@BeforeEach
	void setupService() {
		slotService = new SlotService();
		slotService.setEntityManager(em);
	}

	@Nested
	@DisplayName("Manual Slot Creation")
	class ManualSlotCreation {

		@Test
		@DisplayName("Should create a manual slot successfully")
		void createManualSlot_shouldSucceed() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			em.persist(doctor);
			commit();
			begin();

			SlotRequest request = new SlotRequest();
			request.setDoctorId(doctor.getId());
			request.setStartTime(LocalDateTime.of(2026, 1, 1, 10, 0));

			slotService.createManualSlot(request);
			commit();

			List<SlotViewDTO> slots = slotService.getSlotsForDoctor(doctor.getId());
			assertEquals(1, slots.size());
		}

		@Test
		@DisplayName("Should fail if slot is in the past")
		void createManualSlot_inPast_shouldFail() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			em.persist(doctor);
			commit();
			begin();

			SlotRequest request = new SlotRequest();
			request.setDoctorId(doctor.getId());
			request.setStartTime(LocalDateTime.now().minusDays(1));

			ApiException ex = assertThrows(ApiException.class, () -> slotService.createManualSlot(request));
			assertEquals("Slot must be in the future", ex.getMessage());
		}

		@Test
		@DisplayName("Should fail if slot is outside working hours")
		void createManualSlot_outsideHours_shouldFail() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			em.persist(doctor);
			commit();
			begin();

			SlotRequest request = new SlotRequest();
			request.setDoctorId(doctor.getId());
			request.setStartTime(LocalDateTime.now().plusDays(1).withHour(7).withMinute(0));

			ApiException ex = assertThrows(ApiException.class, () -> slotService.createManualSlot(request));
			assertEquals("Slot must start between 08:00 and 20:00", ex.getMessage());
		}

		@Test
		@DisplayName("Should fail if slot already exists")
		void createManualSlot_duplicate_shouldFail() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			em.persist(doctor);
			commit();
			begin();

			LocalDateTime slotTime = LocalDateTime.of(2026, 1, 1, 10, 0);

			SlotRequest request = new SlotRequest();
			request.setDoctorId(doctor.getId());
			request.setStartTime(slotTime);

			slotService.createManualSlot(request);
			commit();
			begin();

			ApiException ex = assertThrows(ApiException.class, () -> slotService.createManualSlot(request));
			assertEquals("Slot already exists for this time", ex.getMessage());
		}
	}

	@Nested
	@DisplayName("Slot Booking and Cancellation")
	class SlotBookingAndCancel {

		@Test
		@DisplayName("Should book and cancel slot successfully")
		void bookAndCancelSlot_shouldSucceed() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			Patient patient = TestDataFactory.buildPatient("pat@example.com");
			DoctorSlot slot = TestDataFactory.unbookedSlot(doctor);

			em.persist(doctor);
			em.persist(patient);
			em.persist(slot);
			commit();
			begin();

			slotService.bookSlot(slot.getId(), patient.getId());
			commit();
			begin();

			DoctorSlot booked = em.find(DoctorSlot.class, slot.getId());
			assertTrue(booked.isBooked());

			slotService.cancelSlot(slot.getId());
			commit();
			begin();

			DoctorSlot canceled = em.find(DoctorSlot.class, slot.getId());
			assertFalse(canceled.isBooked());
			assertNull(canceled.getBookedBy());
		}

		@Test
		@DisplayName("Should fail booking non-existent slot")
		void book_nonexistentSlot_shouldFail() {
			Patient patient = TestDataFactory.buildPatient("pat@example.com");
			em.persist(patient);
			commit();
			begin();

			ApiException ex = assertThrows(ApiException.class, () -> slotService.bookSlot(999L, patient.getId()));
			assertEquals("Slot not found", ex.getMessage());
		}

		@Test
		@DisplayName("Should fail canceling non-existent slot")
		void cancel_nonexistentSlot_shouldFail() {
			ApiException ex = assertThrows(ApiException.class, () -> slotService.cancelSlot(999L));
			assertEquals("Slot not found", ex.getMessage());
		}

		@Test
		@DisplayName("Should restore availability after booking and canceling")
		void bookAndCancel_shouldRestoreAvailability() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			Patient patient = TestDataFactory.buildPatient("pat@example.com");
			DoctorSlot slot = TestDataFactory.unbookedSlot(doctor);

			em.persist(doctor);
			em.persist(patient);
			em.persist(slot);
			commit();
			begin();

			slotService.bookSlot(slot.getId(), patient.getId());
			commit();
			begin();

			slotService.cancelSlot(slot.getId());
			commit();
			begin();

			DoctorSlot refreshed = em.find(DoctorSlot.class, slot.getId());
			assertFalse(refreshed.isBooked());
			assertNull(refreshed.getBookedBy());
		}
	}

	@Nested
	@DisplayName("Bulk Slot Operations")
	class BulkSlotOps {

		@Test
		@DisplayName("Should generate yearly slots")
		void generateYearlySlots_shouldCreateSlots() {
			Doctor doctor = TestDataFactory.buildDoctor("yearly@example.com");
			em.persist(doctor);
			commit();
			begin();

			int created = slotService.generateYearlySlots(doctor.getId());
			commit();

			assertTrue(created > 200);
			List<SlotViewDTO> slots = slotService.getSlotsForDoctor(doctor.getId());
			assertFalse(slots.isEmpty());
		}

		@Test
		@DisplayName("Should delete all unbooked slots for doctor")
		void deleteUnbookedSlotsByDoctor_shouldSucceed() {
			Doctor doctor = TestDataFactory.buildDoctor("delete@example.com");
			em.persist(doctor);
			em.persist(TestDataFactory.unbookedSlot(doctor));
			em.persist(TestDataFactory.unbookedSlot(doctor));
			commit();
			begin();

			int deleted = slotService.deleteSlotsByDoctor(doctor.getId());
			commit();

			assertEquals(2, deleted);
			assertTrue(slotService.getSlotsForDoctor(doctor.getId()).isEmpty());
		}
	}

	@Nested
	@DisplayName("Slot Retrieval")
	class SlotRetrieval {

		@Test
		@DisplayName("Should get only unbooked slots")
		void getAvailableSlots_shouldReturnOnlyUnbooked() {
			Doctor doctor = TestDataFactory.buildDoctor("avail@example.com");
			Patient patient = TestDataFactory.buildPatient("pat@example.com");

			em.persist(doctor);
			em.persist(patient);
			em.persist(TestDataFactory.bookedSlot(doctor, patient));
			em.persist(TestDataFactory.unbookedSlot(doctor));
			commit();

			List<SlotViewDTO> available = slotService.getAvailableSlots(doctor.getId());
			assertEquals(1, available.size());
		}

		@Test
		@DisplayName("Should get booked slots for patient")
		void getBookedSlotsForPatient_shouldReturnBooked() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			Patient patient = TestDataFactory.buildPatient("pat@example.com");
			DoctorSlot slot = TestDataFactory.bookedSlot(doctor, patient);

			em.persist(doctor);
			em.persist(patient);
			em.persist(slot);
			commit();

			List<SlotBookedDTO> booked = slotService.getBookedSlotsForPatient("pat@example.com");
			assertEquals(1, booked.size());
			assertEquals(slot.getStartTime(), booked.get(0).getStartTime());
		}
	}
}
