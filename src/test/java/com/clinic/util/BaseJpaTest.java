package com.clinic.util;

import jakarta.persistence.*;
import org.junit.jupiter.api.*;

public abstract class BaseJpaTest {
	protected static EntityManagerFactory emf;
	protected EntityManager em;

	@BeforeAll
	public static void initEMF() {
		emf = Persistence.createEntityManagerFactory("ClinicTestPU");
	}

	@AfterAll
	public static void closeEMF() {
		if (emf != null && emf.isOpen()) {
			emf.close();
		}
	}

	@BeforeEach
	public void setUp() {
		em = emf.createEntityManager();
		em.getTransaction().begin();

		em.createQuery("DELETE FROM DoctorSlot").executeUpdate();
		em.createQuery("DELETE FROM Patient").executeUpdate();
		em.createQuery("DELETE FROM Doctor").executeUpdate();
		em.createQuery("DELETE FROM Admin").executeUpdate();

		em.getTransaction().commit();
		em.getTransaction().begin();
	}

	@AfterEach
	public void tearDown() {
		if (em.getTransaction().isActive()) {
			em.getTransaction().rollback();
		}
		if (em.isOpen()) {
			em.close();
		}
	}

	protected void begin() {
		em.getTransaction().begin();
	}

	protected void commit() {
		em.getTransaction().commit();
	}
}
