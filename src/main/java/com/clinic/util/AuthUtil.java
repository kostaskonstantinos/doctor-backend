package com.clinic.util;

import com.clinic.exception.ApiException;
import com.clinic.exception.ErrorCode;
import jakarta.ws.rs.core.SecurityContext;

// HELPERS FOR AUTHORIZATION 
public class AuthUtil {

	public static void requireAdminn(SecurityContext context) {
		if (!context.isUserInRole("admin")) {
			throw new ApiException(ErrorCode.UNAUTHORIZED, "Only admin is authorized");
		}
	}

	public static void requireAdminOrMatchingDoctor(SecurityContext securityContext, String targetDoctorEmail) {
		String callerEmail = securityContext.getUserPrincipal().getName();

		if (securityContext.isUserInRole("admin")) {
			return; // Admins are always allowed
		}

		if (securityContext.isUserInRole("doctor") && callerEmail.equals(targetDoctorEmail)) {
			return; // Specific doctor is allowed
		}

		throw new ApiException(ErrorCode.UNAUTHORIZED, "Not authorized to update this profile");
	}

	public static void requireAdminOrMatchingPatient(SecurityContext securityContext, String targetPatientEmail) {
		String callerEmail = securityContext.getUserPrincipal().getName();

		if (securityContext.isUserInRole("admin")) {
			return; // Admins are always allowed
		}

		if (securityContext.isUserInRole("patient") && callerEmail.equals(targetPatientEmail)) {
			return; // Specific doctor is allowed
		}

		throw new ApiException(ErrorCode.UNAUTHORIZED, "Not authorized to update this profile");
	}

	public static String requireEmail(SecurityContext context) {
		if (context == null || context.getUserPrincipal() == null) {
			throw new ApiException(ErrorCode.UNAUTHORIZED, "Token missing or invalid");
		}
		String email = context.getUserPrincipal().getName();
		if (email == null || email.trim().isEmpty()) {
			throw new ApiException(ErrorCode.UNAUTHORIZED, "Email missing from token");
		}

		return email;
	}

	public static void requireAdminOrPatient(SecurityContext context) {
		if (!context.isUserInRole("admin") && !context.isUserInRole("patient")) {
			throw new ApiException(ErrorCode.UNAUTHORIZED, "Only Admin or Patient can access this resource.");
		}
	}

	public static void requireAdminOrDoctor(SecurityContext context) {
		if (!context.isUserInRole("admin") && !context.isUserInRole("doctor")) {
			throw new ApiException(ErrorCode.UNAUTHORIZED, "Only Admin or doctors can access this resource.");
		}
	}

	public static void requirePatientDoctorOrAdmin(SecurityContext context, String slotDoctorEmail,
			String patientEmailApplicable) {
		//i should pay attention in case a user has more than one roles
		String role = context.isUserInRole("admin") ? "admin"
				: context.isUserInRole("patient") ? "patient" : context.isUserInRole("doctor") ? "doctor" : null;

		String requesterEmail = context.getUserPrincipal().getName();

		boolean isAdmin = "admin".equals(role);
		boolean isPatient = "patient".equals(role) && requesterEmail.equals(patientEmailApplicable);
		boolean isDoctor = "doctor".equals(role) && requesterEmail.equals(slotDoctorEmail);

		if (isAdmin || isPatient || isDoctor)
			return;

		throw new ApiException(ErrorCode.UNAUTHORIZED, "Not authorized to manage this slot.");
	}

}
