package com.clinic.rest;

import com.clinic.adapter.PatientAdapter;
import com.clinic.dto.PatientDTO;
import com.clinic.dto.PatientUpdateDTO;
import com.clinic.dto.PatientViewDTO;
import com.clinic.dto.SuccessResponse;
import com.clinic.entity.Patient;
import com.clinic.exception.ApiException;
import com.clinic.exception.ErrorCode;
import com.clinic.service.PatientService;
import com.clinic.util.BaseJpaTest;
import com.clinic.util.StubSecurityContext;
import com.clinic.util.TestDataFactory;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PatientResource Test")
public class PatientResourceTest extends BaseJpaTest {

	private PatientService patientService;
	private PatientResource patientResource;

	@BeforeEach
	void setup() {
		patientService = new PatientService();
		patientService.setEntityManager(em);
		patientService.setPatientAdapter(new PatientAdapter());

		patientResource = new PatientResource();
		patientResource.setPatientService(patientService);
	}

	// ------------------------------
	@Nested
	@DisplayName("Patient Registration")
	class Registration {

		@Test
		@DisplayName("Should register patient with valid data")
		void registerPatient_shouldSucceed_withValidData() {
			PatientDTO dto = TestDataFactory.samplePatientDTO();

			Response response = patientResource.createPatient(dto);

			assertEquals(201, response.getStatus());
			assertTrue(response.getEntity() instanceof SuccessResponse);

			SuccessResponse success = (SuccessResponse) response.getEntity();
			assertEquals(ErrorCode.PATIENT_CREATED.name(), success.getCode());
		}
	}

	// ------------------------------
	@Nested
	@DisplayName("Patient Deletion")
	class Deletion {

		@Test
		@DisplayName("Should delete patient as admin")
		void deletePatient_shouldSucceed_forAdmin() {
			Patient patient = TestDataFactory.buildPatient("john@clinic.com");
			em.persist(patient);
			commit();
			begin();

			patientResource.securityContext = new StubSecurityContext(true, false, false, "admin@clinic.com");

			Response response = patientResource.deletePatient(patient.getId());

			assertEquals(200, response.getStatus());
			SuccessResponse success = (SuccessResponse) response.getEntity();
			assertEquals(ErrorCode.PATIENT_DELETED.name(), success.getCode());

			assertNull(em.find(Patient.class, patient.getId()));
		}

		@Test
		@DisplayName("Should fail to delete patient if not admin")
		void deletePatient_shouldFail_ifNotAdmin() {
			Patient patient = TestDataFactory.buildPatient("notadmin@clinic.com");
			em.persist(patient);
			commit();
			begin();

			patientResource.securityContext = new StubSecurityContext(false, true, false, "notadmin@clinic.com");

			ApiException ex = assertThrows(ApiException.class, () -> patientResource.deletePatient(patient.getId()));

			assertEquals(ErrorCode.UNAUTHORIZED, ex.getCode());
		}
	}

	// ------------------------------
	@Nested
	@DisplayName("Patient Update")
	class Update {

		@Test
		@DisplayName("Should update patient info for matching patient")
		void updatePatient_shouldSucceed_forMatchingPatient() {
			Patient patient = TestDataFactory.buildPatient("pat@clinic.com");
			em.persist(patient);
			commit();
			begin();

			patientResource.securityContext = new StubSecurityContext(false, false, true, "pat@clinic.com");

			PatientUpdateDTO dto = new PatientUpdateDTO();
			dto.setName("Updated Name");

			Response response = patientResource.updatePatient(patient.getId(), dto);

			assertEquals(200, response.getStatus());
			SuccessResponse success = (SuccessResponse) response.getEntity();
			assertEquals(ErrorCode.PATIENT_UPDATED.name(), success.getCode());
		}

		@Test
		@DisplayName("Should fail to update patient if caller is not matching patient")
		void updatePatient_shouldFail_forOtherPatient() {
			Patient patient = TestDataFactory.buildPatient("a@clinic.com");
			em.persist(patient);
			commit();
			begin();

			patientResource.securityContext = new StubSecurityContext(false, false, true, "b@clinic.com");

			PatientUpdateDTO dto = new PatientUpdateDTO();
			dto.setName("Not allowed");

			ApiException ex = assertThrows(ApiException.class,
					() -> patientResource.updatePatient(patient.getId(), dto));
			assertEquals(ErrorCode.UNAUTHORIZED, ex.getCode());
		}
	}

	// ------------------------------
	@Nested
	@DisplayName("Patient Fetching")
	class Fetching {

		@Test
		@DisplayName("Should return all patients for admin")
		void getAllPatients_shouldSucceed_forAdmin() {
			em.persist(TestDataFactory.buildPatient("a@clinic.com"));
			em.persist(TestDataFactory.buildPatient("b@clinic.com"));
			commit();
			begin();

			patientResource.securityContext = new StubSecurityContext(true, false, false, "admin@clinic.com");

			Response response = patientResource.getAllPatients();

			assertEquals(200, response.getStatus());
			assertTrue(response.getEntity() instanceof List<?>);
		}

		@Test
		@DisplayName("Should fail to get all patients when unauthenticated")
		void getAllPatients_shouldFail_forUnauthenticated() {
			patientResource.securityContext = new StubSecurityContext(false, false, false, null);

			ApiException ex = assertThrows(ApiException.class, () -> patientResource.getAllPatients());

			assertEquals(ErrorCode.UNAUTHORIZED, ex.getCode());
		}

		@Test
		@DisplayName("Should return current patient profile")
		void getCurrentPatient_shouldReturnOwnProfile() {
			Patient patient = TestDataFactory.buildPatient("me@clinic.com");
			em.persist(patient);
			commit();
			begin();

			patientResource.securityContext = new StubSecurityContext(false, false, true, "me@clinic.com");

			Response response = patientResource.getCurrentPatient();

			assertEquals(200, response.getStatus());
			assertTrue(response.getEntity() instanceof PatientViewDTO);

			PatientViewDTO dto = (PatientViewDTO) response.getEntity();
			assertEquals("me@clinic.com", dto.getEmail());
		}

		@Test
		@DisplayName("Should fail to return profile when unauthenticated")
		void getCurrentPatient_shouldFail_whenUnauthenticated() {
			patientResource.securityContext = new StubSecurityContext(false, false, false, null);

			ApiException ex = assertThrows(ApiException.class, () -> patientResource.getCurrentPatient());

			assertEquals(ErrorCode.UNAUTHORIZED, ex.getCode());
		}
	}
}
