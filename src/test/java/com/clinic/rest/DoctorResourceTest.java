package com.clinic.rest;

import com.clinic.adapter.DoctorAdapter;
import com.clinic.dto.DoctorDTO;
import com.clinic.dto.DoctorUpdateDTO;
import com.clinic.dto.DoctorViewDTO;
import com.clinic.dto.SuccessResponse;
import com.clinic.entity.Doctor;
import com.clinic.exception.ApiException;
import com.clinic.exception.ErrorCode;
import com.clinic.service.DoctorService;
import com.clinic.util.BaseJpaTest;
import com.clinic.util.StubSecurityContext;
import com.clinic.util.TestDataFactory;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DoctorResource Test")
public class DoctorResourceTest extends BaseJpaTest {

	private DoctorService doctorService;
	private DoctorResource doctorResource;

	@BeforeEach
	void setup() {
		doctorService = new DoctorService();
		doctorService.setEntityManager(em);
		doctorService.setDoctorAdapter(new DoctorAdapter());

		doctorResource = new DoctorResource();
		doctorResource.setDoctorService(doctorService);
	}

	// ------------------------------
	@Nested
	@DisplayName("Doctor Creation")
	class DoctorCreation {

		@Test
		@DisplayName("Should register doctor as admin")
		void createDoctor_shouldSucceed_forAdmin() {
			doctorResource.securityContext = new StubSecurityContext(true, false, false, "admin@example.com");

			DoctorDTO dto = TestDataFactory.sampleDoctorDTO();
			Response response = doctorResource.createDoctor(dto);

			assertEquals(201, response.getStatus());
			SuccessResponse success = (SuccessResponse) response.getEntity();
			assertEquals(ErrorCode.DOCTOR_CREATED.name(), success.getCode());
		}
	}

	// ------------------------------
	@Nested
	@DisplayName("Doctor Update")
	class DoctorUpdate {

		@Test
		@DisplayName("Should update doctor as matching doctor")
		void updateDoctor_shouldSucceed_forMatchingDoctor() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			em.persist(doctor);
			commit();
			begin();

			doctorResource.securityContext = new StubSecurityContext(false, true, false, "doc@example.com");

			DoctorUpdateDTO dto = new DoctorUpdateDTO();
			dto.setName("Updated Doc");
			dto.setPassword("newpass123");

			Response response = doctorResource.updateDoctor(doctor.getId(), dto);

			assertEquals(200, response.getStatus());
			SuccessResponse success = (SuccessResponse) response.getEntity();
			assertEquals(ErrorCode.DOCTOR_UPDATED.name(), success.getCode());
		}

		@Test
		@DisplayName("Should fail to update doctor as different doctor")
		void updateDoctor_shouldFail_forOtherDoctor() {
			Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
			em.persist(doctor);
			commit();
			begin();

			doctorResource.securityContext = new StubSecurityContext(false, true, false, "anotherdoc@example.com");

			DoctorUpdateDTO dto = new DoctorUpdateDTO();
			dto.setName("Hacker Doc");

			ApiException ex = assertThrows(ApiException.class, () -> doctorResource.updateDoctor(doctor.getId(), dto));
			assertEquals(ErrorCode.UNAUTHORIZED, ex.getCode());
		}
	}

	// ------------------------------
	@Nested
	@DisplayName("Doctor Deletion")
	class DoctorDeletion {

		@Test
		@DisplayName("Should delete doctor with no bookings as admin")
		void deleteDoctor_shouldSucceed_whenNoBookedSlots() {
			Doctor doctor = TestDataFactory.buildDoctor("delete@doc.com");
			em.persist(doctor);
			commit();
			begin();

			doctorResource.securityContext = new StubSecurityContext(true, false, false, "admin@example.com");

			Response response = doctorResource.deleteDoctor(doctor.getId());

			assertEquals(200, response.getStatus());
			SuccessResponse success = (SuccessResponse) response.getEntity();
			assertEquals(ErrorCode.DOCTOR_DELETED.name(), success.getCode());

			assertNull(em.find(Doctor.class, doctor.getId()));
		}
	}

	// ------------------------------
	@Nested
	@DisplayName("Doctor Fetching")
	class DoctorFetching {

		@Test
		@DisplayName("Should return all doctors as admin")
		void getAllDoctors_shouldSucceed_forAdmin() {
			doctorResource.securityContext = new StubSecurityContext(true, false, false, "admin@example.com");

			Response response = doctorResource.getAllDoctors();
			assertEquals(200, response.getStatus());
		}

		@Test
		@DisplayName("Should return all doctors as patient")
		void getAllDoctors_shouldSucceed_forPatient() {
			doctorResource.securityContext = new StubSecurityContext(false, false, true, "pat@example.com");

			Response response = doctorResource.getAllDoctors();
			assertEquals(200, response.getStatus());
		}

		@Test
		@DisplayName("Should fail to return doctors for unauthenticated user")
		void getAllDoctors_shouldFail_forUnauthenticated() {
			doctorResource.securityContext = new StubSecurityContext(false, false, false, null);

			ApiException ex = assertThrows(ApiException.class, () -> doctorResource.getAllDoctors());

			assertEquals(ErrorCode.UNAUTHORIZED, ex.getCode());
		}

		@Test
		@DisplayName("Should return own profile as doctor")
		void getCurrentDoctor_shouldReturnOwnProfile() {
			Doctor doctor = TestDataFactory.buildDoctor("self@doc.com");
			em.persist(doctor);
			commit();
			begin();

			doctorResource.securityContext = new StubSecurityContext(false, true, false, "self@doc.com");

			Response response = doctorResource.getCurrentDoctor();
			assertEquals(200, response.getStatus());

			DoctorViewDTO dto = (DoctorViewDTO) response.getEntity();
			assertEquals("self@doc.com", dto.getEmail());
		}

		@Test
		@DisplayName("Should fail to fetch profile if unauthenticated")
		void getCurrentDoctor_shouldFail_whenUnauthenticated() {
			doctorResource.securityContext = new StubSecurityContext(false, false, false, null);

			ApiException ex = assertThrows(ApiException.class, () -> doctorResource.getCurrentDoctor());

			assertEquals(ErrorCode.UNAUTHORIZED, ex.getCode());
		}
	}
}
