package com.clinic.service;

import com.clinic.adapter.PatientAdapter;
import com.clinic.dto.*;
import com.clinic.entity.*;
import com.clinic.exception.ApiException;
import com.clinic.util.BaseJpaTest;
import com.clinic.util.TestDataFactory;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PatientService Test")
public class PatientServiceTest extends BaseJpaTest {

	private PatientService patientService;

	@BeforeEach
	void setupService() {
		patientService = new PatientService();
		patientService.setEntityManager(em);
		patientService.setPatientAdapter(new PatientAdapter());
	}

	// -------------------------
	@Nested
	@DisplayName("Registration")
	class RegistrationTests {

		@Test
		@DisplayName("Should register patient and fetch by email")
		void registerPatient_andFetchByEmail_shouldSucceed() {
			PatientDTO dto = TestDataFactory.samplePatientDTO();
			patientService.registerPatient(dto);
			commit();

			PatientViewDTO fetched = patientService.getByEmail("pat@example.com");
			assertEquals("John Doe", fetched.getName());
		}

		@Test
		@DisplayName("Should fail registering duplicate email")
		void registerPatient_withDuplicateEmail_shouldFail() {
			PatientDTO dto = TestDataFactory.samplePatientDTO();
			patientService.registerPatient(dto);
			commit();
			begin();

			ApiException ex = assertThrows(ApiException.class, () -> patientService.registerPatient(dto));
			assertEquals("Email already registered", ex.getMessage());
		}
	}

	// -------------------------
	@Nested
	@DisplayName("Fetching")
	class FetchingTests {

		@Test
		@DisplayName("Should return all patients")
		void getAllPatients_shouldReturnList() {
			em.persist(TestDataFactory.buildPatient("pat@example.com"));
			commit();

			List<PatientViewDTO> patients = patientService.getAllPatients();
			assertFalse(patients.isEmpty());
		}

		@Test
		@DisplayName("Should throw if patient not found by email")
		void getByEmail_notFound_shouldThrow() {
			ApiException ex = assertThrows(ApiException.class, () -> patientService.getByEmail("ghost@nowhere.com"));
			assertEquals("Patient not found", ex.getMessage());
		}
	}

	// -------------------------
	@Nested
	@DisplayName("Updates")
	class UpdateTests {

		@Test
		@DisplayName("Should update patient with valid data")
		void updatePatient_withValidData_shouldSucceed() {
			Patient patient = TestDataFactory.buildPatient("pat@example.com");
			em.persist(patient);
			commit();
			begin();

			PatientUpdateDTO update = new PatientUpdateDTO();
			update.setName("Updated");

			patientService.updatePatient(patient.getId(), update);
			commit();
			begin();

			PatientViewDTO updated = patientService.getByEmail("pat@example.com");
			assertEquals("Updated", updated.getName());
		}

		@Test
		@DisplayName("Should throw when updating with invalid ID")
		void updatePatient_withInvalidId_shouldThrow() {
			PatientUpdateDTO dto = new PatientUpdateDTO();
			dto.setName("Fail");

			ApiException ex = assertThrows(ApiException.class, () -> patientService.updatePatient(999L, dto));
			assertEquals("Patient not found", ex.getMessage());
		}
	}

	// -------------------------
	@Nested
	@DisplayName("Deletion")
	class DeletionTests {

		@Test
		@DisplayName("Should delete patient without bookings")
		void deletePatient_withoutBookings_shouldSucceed() {
			Patient patient = TestDataFactory.buildPatient("pat@example.com");
			em.persist(patient);
			commit();
			begin();

			patientService.deletePatientIfAllowed(patient.getId());
			commit();
			begin();

			assertThrows(ApiException.class, () -> patientService.findById(patient.getId()));
		}

		@Test
		@DisplayName("Should fail to delete patient with booked appointments")
		void deletePatient_withBookings_shouldFail() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			Patient patient = TestDataFactory.buildPatient("john@example.com");
			DoctorSlot slot = TestDataFactory.bookedSlot(doctor, patient);

			em.persist(doctor);
			em.persist(patient);
			em.persist(slot);
			commit();
			begin();

			ApiException ex = assertThrows(ApiException.class,
					() -> patientService.deletePatientIfAllowed(patient.getId()));
			assertEquals("Patient has booked appointments", ex.getMessage());
		}
	}
}
