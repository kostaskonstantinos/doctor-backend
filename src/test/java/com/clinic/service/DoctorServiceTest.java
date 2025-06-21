package com.clinic.service;

import com.clinic.adapter.DoctorAdapter;
import com.clinic.dto.*;
import com.clinic.entity.*;
import com.clinic.exception.ApiException;
import com.clinic.util.BaseJpaTest;
import com.clinic.util.PasswordUtil;
import com.clinic.util.TestDataFactory;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DoctorService Test")
public class DoctorServiceTest extends BaseJpaTest {

	private DoctorService doctorService;

	@BeforeEach
	void setupService() {
		doctorService = new DoctorService();
		doctorService.setEntityManager(em);
		doctorService.setDoctorAdapter(new DoctorAdapter());
	}

	// ------------------------------
	@Nested
	@DisplayName("Doctor Creation")
	class Creation {

		@Test
		@DisplayName("Should create doctor and fetch by email")
		void createDoctor_andFetchByEmail_shouldSucceed() {
			DoctorDTO dto = TestDataFactory.sampleDoctorDTO();
			doctorService.createDoctor(dto);
			commit();

			DoctorViewDTO fetched = doctorService.getDoctorByEmail("doc@example.com");
			assertEquals("Doc John", fetched.getName());
		}

		@Test
		@DisplayName("Should fail to create doctor with duplicate email")
		void createDoctor_withDuplicateEmail_shouldFail() {
			DoctorDTO dto = TestDataFactory.sampleDoctorDTO();
			doctorService.createDoctor(dto);
			commit();
			begin();

			ApiException ex = assertThrows(ApiException.class, () -> doctorService.createDoctor(dto));
			assertEquals("Doctor already exists", ex.getMessage());
		}
	}

	// ------------------------------
	@Nested
	@DisplayName("Doctor Updates")
	class Updates {

		@Test
		@DisplayName("Should update doctor with valid data")
		void updateDoctor_withValidData_shouldSucceed() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			em.persist(doctor);
			commit();
			begin();

			DoctorUpdateDTO dto = new DoctorUpdateDTO();
			dto.setName("New Name");
			dto.setPassword("newPass123");

			doctorService.updateDoctor(doctor.getId(), dto);
			commit();

			Doctor updated = em.find(Doctor.class, doctor.getId());
			assertEquals("New Name", updated.getName());
			assertTrue(PasswordUtil.checkPassword("newPass123", updated.getPassword()));
		}

		@Test
		@DisplayName("Should throw when updating nonexistent doctor")
		void updateDoctor_withInvalidId_shouldThrow() {
			DoctorUpdateDTO dto = new DoctorUpdateDTO();
			dto.setName("Should Fail");

			ApiException ex = assertThrows(ApiException.class, () -> doctorService.updateDoctor(999L, dto));
			assertEquals("Doctor not found", ex.getMessage());
		}

		@Test
		@DisplayName("Should not change anything with empty DTO")
		void updateDoctor_withEmptyDTO_shouldNotChangeAnything() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			em.persist(doctor);
			commit();
			begin();

			doctorService.updateDoctor(doctor.getId(), new DoctorUpdateDTO());
			commit();
			begin();

			DoctorViewDTO after = doctorService.getDoctorByEmail("doc@example.com");
			assertEquals(doctor.getName(), after.getName());
		}
	}

	// ------------------------------
	@Nested
	@DisplayName("Doctor Deletion")
	class Deletion {

		@Test
		@DisplayName("Should delete doctor with no bookings")
		void deleteDoctor_withoutBookings_shouldSucceed() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			em.persist(doctor);
			commit();
			begin();

			doctorService.deleteDoctorIfAllowed(doctor.getId());
			commit();

			assertNull(em.find(Doctor.class, doctor.getId()));
		}

		@Test
		@DisplayName("Should fail to delete doctor with bookings")
		void deleteDoctor_withBookings_shouldFail() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			Patient patient = TestDataFactory.buildPatient("john@example.com");
			DoctorSlot slot = TestDataFactory.bookedSlot(doctor, patient);

			em.persist(doctor);
			em.persist(patient);
			em.persist(slot);
			commit();
			begin();

			ApiException ex = assertThrows(ApiException.class,
					() -> doctorService.deleteDoctorIfAllowed(doctor.getId()));
			assertEquals("Doctor has booked appointments", ex.getMessage());
		}
	}

	// ------------------------------
	@Nested
	@DisplayName("Doctor Fetching")
	class Fetching {

		@Test
		@DisplayName("Should return all doctors")
		void getAllDoctors_shouldReturnList() {
			em.persist(TestDataFactory.buildDoctor("doc@example.com"));
			commit();

			List<DoctorViewDTO> list = doctorService.getAllDoctors();
			assertFalse(list.isEmpty());
		}

		@Test
		@DisplayName("Should throw when doctor not found by email")
		void getDoctorByEmail_notFound_shouldThrow() {
			ApiException ex = assertThrows(ApiException.class,
					() -> doctorService.getDoctorByEmail("ghost@nowhere.com"));

			assertEquals("Doctor not found", ex.getMessage());
		}
	}
}
