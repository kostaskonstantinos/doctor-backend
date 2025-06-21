package com.clinic.util;

import com.clinic.exception.ApiException;
import com.clinic.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthUtilTest {

	@Test
	@DisplayName("Require admin should throw if not admin")
	void requireAdmin_should_throw_if_not_admin() {
		StubSecurityContext context = new StubSecurityContext(false, false, false, "user@example.com");

		ApiException ex = assertThrows(ApiException.class, () -> AuthUtil.requireAdminn(context));
		assertEquals(ErrorCode.UNAUTHORIZED, ex.getCode());
	}

	@Test
	@DisplayName("Require admin should pass for admin")
	void requireAdmin_should_pass_for_admin() {
		StubSecurityContext context = new StubSecurityContext(true, false, false, "admin@example.com");

		assertDoesNotThrow(() -> AuthUtil.requireAdminn(context));
	}

	@Test
	@DisplayName("Require email should give email")
	void requireEmail_should_return_email() {
		StubSecurityContext context = new StubSecurityContext(false, false, false, "john@example.com");

		String email = AuthUtil.requireEmail(context);
		assertEquals("john@example.com", email);
	}

	@Test
	@DisplayName("Require admin or matching doctor should pass if admin")
	void requireAdminOrMatchingDoctor_should_pass_if_admin() {
		StubSecurityContext context = new StubSecurityContext(true, false, false, "admin@example.com");

		assertDoesNotThrow(() -> AuthUtil.requireAdminOrMatchingDoctor(context, "any@example.com"));
	}

	@Test
	@DisplayName("Require admin or matching doctor should pass if matching doctor")
	void requireAdminOrMatchingDoctor_should_pass_if_matching_doctor() {
		StubSecurityContext context = new StubSecurityContext(false, true, false, "doc@example.com");

		assertDoesNotThrow(() -> AuthUtil.requireAdminOrMatchingDoctor(context, "doc@example.com"));
	}

	@Test
	@DisplayName("Require admin or matching doctor should fail if mismatch")
	void requireAdminOrMatchingDoctor_should_fail_if_mismatch() {
		StubSecurityContext context = new StubSecurityContext(false, true, false, "anotherdoc@example.com");

		ApiException ex = assertThrows(ApiException.class,
				() -> AuthUtil.requireAdminOrMatchingDoctor(context, "doc@example.com"));
		assertEquals(ErrorCode.UNAUTHORIZED, ex.getCode());
	}
}
