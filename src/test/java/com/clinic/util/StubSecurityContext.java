package com.clinic.util;

import java.security.Principal;

import jakarta.ws.rs.core.SecurityContext;

public class StubSecurityContext implements SecurityContext {
	private final boolean isAdmin;
	private final boolean isDoctor;
	private final boolean isPatient;
	private final String principalName;

	public StubSecurityContext(boolean isAdmin, boolean isDoctor, boolean isPatient, String principalName) {
		this.isAdmin = isAdmin;
		this.isDoctor = isDoctor;
		this.isPatient = isPatient;
		this.principalName = principalName;
	}

	@Override
	public Principal getUserPrincipal() {
		return () -> principalName;
	}

	@Override
	public boolean isUserInRole(String role) {
		switch (role) {
		case "admin":
			return isAdmin;
		case "doctor":
			return isDoctor;
		case "patient":
			return isPatient;
		default:
			return false;
		}
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public String getAuthenticationScheme() {
		return null;
	}
}