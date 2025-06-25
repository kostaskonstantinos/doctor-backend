package com.clinic.service;

import com.clinic.dto.LoginResponse;
import com.clinic.dto.UserCredentials;
import com.clinic.entity.Admin;
import com.clinic.entity.Doctor;
import com.clinic.entity.Patient;
import com.clinic.util.JwtUtil;
//import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class LoginService {

	@PersistenceContext(unitName = "ClinicPU")
	private EntityManager em;

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	public LoginResponse authenticate(UserCredentials credentials) {
		if (credentials == null || credentials.getEmail() == null || credentials.getPassword() == null) {
			return null;
		}
		String email = credentials.getEmail();
		String password = credentials.getPassword();

		// Check Admin
		Admin admin = em.createQuery("SELECT a FROM Admin a WHERE a.email = :email", Admin.class)
				.setParameter("email", email).getResultStream().findFirst().orElse(null);
		if (admin != null && BCrypt.checkpw(password, admin.getPassword())) {
			return new LoginResponse(JwtUtil.generateToken(email, "admin"), "admin");
		}

		// Check Doctor
		Doctor doctor = em.createQuery("SELECT d FROM Doctor d WHERE d.email = :email", Doctor.class)
				.setParameter("email", email).getResultStream().findFirst().orElse(null);
		if (doctor != null && BCrypt.checkpw(password, doctor.getPassword())) {
			return new LoginResponse(JwtUtil.generateToken(email, "doctor"), "doctor");
		}

		// Check Patient
		Patient patient = em.createQuery("SELECT p FROM Patient p WHERE p.email = :email", Patient.class)
				.setParameter("email", email).getResultStream().findFirst().orElse(null);
		if (patient != null && BCrypt.checkpw(password, patient.getPassword())) {
			return new LoginResponse(JwtUtil.generateToken(email, "patient"), "patient");
		}

		return null; // no match
	}
}
