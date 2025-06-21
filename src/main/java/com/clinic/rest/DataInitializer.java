package com.clinic.rest;

import org.mindrot.jbcrypt.BCrypt;
import com.clinic.entity.Admin;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Startup
@Singleton
public class DataInitializer {

	@PersistenceContext(unitName = "ClinicPU")
	private EntityManager em;

	@PostConstruct
	@Transactional
	public void init() {
		String env = System.getProperty("env", "dev");
		if (!env.equals("dev")) {
			System.out.println("Skipping DataInitializer for test environment");
			return;
		}
		try {
			boolean hasAdmin = !em.createQuery("SELECT a FROM Admin a", Admin.class).getResultList().isEmpty();

			if (!hasAdmin) {
				Admin admin = new Admin("admin@example.com", "admin123");
				String hashedPassword = BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt());
				admin.setPassword(hashedPassword);
				em.persist(admin);
				System.out.println("Default admin inserted.");
			}
		} catch (Exception e) {
			System.out.println("Data initialization skipped: " + e.getMessage());
		}
	}
}
