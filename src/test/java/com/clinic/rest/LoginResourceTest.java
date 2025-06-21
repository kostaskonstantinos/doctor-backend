package com.clinic.rest;

import com.clinic.dto.LoginResponse;
import com.clinic.dto.UserCredentials;
import com.clinic.entity.Admin;
import com.clinic.entity.Doctor;
import com.clinic.entity.Patient;
import com.clinic.exception.ApiException;
import com.clinic.exception.ErrorCode;
import com.clinic.service.LoginService;
import com.clinic.util.BaseJpaTest;
import com.clinic.util.PasswordUtil;
import com.clinic.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginResource Test")
class LoginResourceTest extends BaseJpaTest {

	private LoginResource loginResource;

	@BeforeEach
	void setup() {
		LoginService loginService = new LoginService();
		loginService.setEntityManager(em);

		loginResource = new LoginResource();
		loginResource.setLoginService(loginService);
	}

	@Test
	@DisplayName("Login with valid admin credentials should succeed")
	void login_withValidAdmin_shouldSucceed() {
		Admin admin = new Admin("admin@example.com", PasswordUtil.hashPassword("admin123"));
		em.persist(admin);
		commit();
		begin();

		UserCredentials credentials = new UserCredentials("admin@example.com", "admin123");
		LoginResponse response = (LoginResponse) loginResource.login(credentials).getEntity();

		assertEquals("admin", response.getRole());
	}

	@Test
	@DisplayName("Login with valid doctor credentials should succeed")
	void login_withValidDoctor_shouldSucceed() {
		Doctor doctor = TestDataFactory.buildDoctor("doc@example.com");
		em.persist(doctor);
		commit();
		begin();

		UserCredentials credentials = new UserCredentials("doc@example.com", "docPass123");
		LoginResponse response = (LoginResponse) loginResource.login(credentials).getEntity();

		assertEquals("doctor", response.getRole());
	}

	@Test
	@DisplayName("Login with valid patient credentials should succeed")
	void login_withValidPatient_shouldSucceed() {
		Patient patient = TestDataFactory.buildPatient("pat@example.com");
		em.persist(patient);
		commit();
		begin();

		UserCredentials credentials = new UserCredentials("pat@example.com", "securePass123");
		LoginResponse response = (LoginResponse) loginResource.login(credentials).getEntity();
		assertEquals("patient", response.getRole());
	}

	@Test
	@DisplayName("Login with invalid credentials should return 401")
	void login_withInvalidCredentials_shouldReturn401() {
		UserCredentials credentials = new UserCredentials("ghost@example.com", "wrongpass");
		ApiException ex = assertThrows(ApiException.class, () -> loginResource.login(credentials));
		assertEquals(ErrorCode.INVALID_CREDENTIALS, ex.getCode());
		assertEquals("Invalid email or password", ex.getMessage());
	}

}
