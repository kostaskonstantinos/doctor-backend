package com.clinic.service;

import com.clinic.dto.LoginResponse;
import com.clinic.dto.UserCredentials;
import com.clinic.entity.Admin;
import com.clinic.entity.Doctor;
import com.clinic.entity.Patient;
import com.clinic.util.BaseJpaTest;
import com.clinic.util.PasswordUtil;
import com.clinic.util.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest extends BaseJpaTest {

	private LoginService loginService;

	@BeforeEach
	void setUpService() {

		loginService = new LoginService();
		loginService.setEntityManager(em);
	}

	@Test
	@DisplayName("Valid admin credentials should return admin role")
	void authenticate_validAdmin_returnsAdminRole() {
		Admin admin = new Admin("admin@example.com", PasswordUtil.hashPassword("admin123"));
		em.persist(admin);
		commit();

		UserCredentials credentials = new UserCredentials("admin@example.com", "admin123");
		LoginResponse response = loginService.authenticate(credentials);

		assertNotNull(response);
		assertEquals("admin", response.getRole());
	}

	@Test
	@DisplayName("Valid doctor credentials should return doctor role")
	void authenticate_validDoctor_returnsDoctorRole() {
		Doctor doctor = TestDataFactory.buildDoctor("drsmith@example.com");
		em.persist(doctor);
		commit();

		UserCredentials credentials = new UserCredentials("drsmith@example.com", "docPass123");
		LoginResponse response = loginService.authenticate(credentials);

		assertNotNull(response);
		assertEquals("doctor", response.getRole());
	}

	@Test
	@DisplayName("Valid patient credentials should return patient role")
	void authenticate_validPatient_returnsPatientRole() {
		Patient patient = TestDataFactory.buildPatient("alice@example.com");
		em.persist(patient);
		commit();

		UserCredentials credentials = new UserCredentials("alice@example.com", "securePass123");
		LoginResponse response = loginService.authenticate(credentials);

		assertNotNull(response);
		assertEquals("patient", response.getRole());
	}

	@Test
	@DisplayName("Nonexistent email should return null")
	void authenticate_nonexistentEmail_returnsNull() {
		UserCredentials credentials = new UserCredentials("ghost@example.com", "somepass");
		LoginResponse response = loginService.authenticate(credentials);
		assertNull(response);
	}

	@Test
	@DisplayName("Invalid password should return null")
	void authenticate_invalidPassword_returnsNull() {
		Admin admin = new Admin("admin@example.com", PasswordUtil.hashPassword("admin123"));
		em.persist(admin);
		commit();

		UserCredentials credentials = new UserCredentials("admin@example.com", "wrongpass");
		LoginResponse response = loginService.authenticate(credentials);

		assertNull(response);
	}

	@Test
	@DisplayName("Null email and password should return null")
	void authenticate_nullEmailOrPassword_returnsNull() {
		UserCredentials credentials = new UserCredentials(null, null);
		LoginResponse response = loginService.authenticate(credentials);
		assertNull(response);
	}

	@Test
	@DisplayName("Null email should return null")
	void authenticate_nullEmail_returnsNull() {
		UserCredentials credentials = new UserCredentials(null, "admin123");
		LoginResponse response = loginService.authenticate(credentials);
		assertNull(response);
	}

	@Test
	@DisplayName("Null password should return null")
	void authenticate_nullPassword_returnsNull() {
		UserCredentials credentials = new UserCredentials("admin@example.com", null);
		LoginResponse response = loginService.authenticate(credentials);
		assertNull(response);
	}

	@Test
	@DisplayName("Empty credentials should return null")
	void authenticate_emptyCredentials_returnsNull() {
		UserCredentials credentials = new UserCredentials("", "");
		LoginResponse response = loginService.authenticate(credentials);
		assertNull(response);
	}
}
