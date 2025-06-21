package com.clinic.service;

import com.clinic.adapter.SlotAdapter;
import com.clinic.dto.SlotBookedDTO;
import com.clinic.dto.SlotRequest;
import com.clinic.dto.SlotViewDTO;
import com.clinic.entity.Doctor;
import com.clinic.entity.Patient;
import com.clinic.exception.ApiException;
import com.clinic.exception.ErrorCode;
import com.clinic.entity.DoctorSlot;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class SlotService {

	@PersistenceContext(unitName = "ClinicPU")
	private EntityManager em;

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}

	// HELPERS
	public Doctor getDoctorOfSlot(Long slotId) {
		DoctorSlot slot = em.find(DoctorSlot.class, slotId);
		if (slot == null) {
			throw new ApiException(ErrorCode.SLOT_NOT_FOUND, "Slot not found");
		}
		return slot.getDoctor();
	}

	public DoctorSlot findById(Long slotId) {
		DoctorSlot slot = em.find(DoctorSlot.class, slotId);
		if (slot == null) {
			throw new ApiException(ErrorCode.SLOT_NOT_FOUND, "Slot not found");
		}
		return slot;
	}

	public String getPatientEmailById(Long patientId) {
		if (patientId == null) {
			throw new ApiException(ErrorCode.BAD_REQUEST, "Patient ID must not be null");
		}
		Patient patient = em.find(Patient.class, patientId);
		if (patient == null) {
			throw new ApiException(ErrorCode.PATIENT_NOT_FOUND, "patient not found");
		}
		return patient.getEmail();
	}

	// CREATE ALL SLOTS (FOR ONE YEAR MONDAY-FRIDAY)
	@Transactional
	public int generateYearlySlots(Long doctorId) {
		Doctor doctor = em.find(Doctor.class, doctorId);
		if (doctor == null) {
			throw new ApiException(ErrorCode.DOCTOR_NOT_FOUND, "Doctor not found");
		}
		System.out.println("Generating slots for doctorId: " + doctorId);
		LocalDate today = LocalDate.now();
		LocalDate oneYearLater = today.plusYears(1);

		int createdCount = 0;

		for (LocalDate date = today; date.isBefore(oneYearLater); date = date.plusDays(1)) {
			DayOfWeek dow = date.getDayOfWeek();
			if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
				continue;
			}

			for (int hour = 8; hour < 17; hour++) {
				LocalDateTime startTime = date.atTime(hour, 0);

				SlotRequest request = new SlotRequest();
				request.setDoctorId(doctorId);
				request.setStartTime(startTime);

				try {
					System.out.println("Trying slot at " + startTime);

					createManualSlot(request);
					createdCount++;
				} catch (ApiException e) {
					// slot already exists or invalid time – just skip
					if (!e.getCode().equals(ErrorCode.SLOT_ALREADY_EXISTS)
							&& !e.getCode().equals(ErrorCode.SLOT_INVALID_TIME)) {
						throw e; // only suppress expected exceptions
					}
				}
			}
		}

		return createdCount;
	}

	// DELETE ALL SLOTS
	@Transactional
	public int deleteSlotsByDoctor(Long doctorId) {
		Doctor doctor = em.find(Doctor.class, doctorId);
		if (doctor == null) {
			throw new ApiException(ErrorCode.DOCTOR_NOT_FOUND, "Doctor not found");
		}

		// deletable slots
		List<DoctorSlot> slots = em
				.createQuery("SELECT s FROM DoctorSlot s WHERE s.doctor = :doctor AND s.booked = false",
						DoctorSlot.class)
				.setParameter("doctor", doctor).getResultList();

		
		for (DoctorSlot s : slots) {
			em.remove(s);
		}

		return slots.size(); // Return number of slots deleted
	}

	// CREATE ONE SLOT
	@Transactional
	public void createManualSlot(SlotRequest request) {
		// 1. Validate doctor existence
		Doctor doctor = em.find(Doctor.class, request.getDoctorId());
		if (doctor == null) {
			throw new ApiException(ErrorCode.DOCTOR_NOT_FOUND, "Doctor not found");
		}

		// 2. Validate startTime business rules
		LocalDateTime start = request.getStartTime();

		if (start.isBefore(LocalDateTime.now())) {
			throw new ApiException(ErrorCode.SLOT_INVALID_TIME, "Slot must be in the future");
		}

		int hour = start.getHour();
		if (hour < 8 || hour > 20) {
			throw new ApiException(ErrorCode.SLOT_INVALID_TIME, "Slot must start between 08:00 and 20:00");
		}

		// 3. Check if slot already exists
		Long count = em
				.createQuery("SELECT COUNT(s) FROM DoctorSlot s WHERE s.doctor = :doctor AND s.startTime = :startTime",
						Long.class)
				.setParameter("doctor", doctor).setParameter("startTime", start).getSingleResult();

		if (count > 0) {
			throw new ApiException(ErrorCode.SLOT_ALREADY_EXISTS, "Slot already exists for this time");
		}

		// 4. Create slot
		DoctorSlot slot = SlotAdapter.toEntity(request, doctor);
		slot.setEndTime(slot.getStartTime().plusHours(1)); // Business rule
		em.persist(slot);
	}

	// DELETE ONE SLOT
	@Transactional
	public void deleteSlotById(Long slotId) {
		DoctorSlot slot = em.find(DoctorSlot.class, slotId);
		if (slot == null) {
			throw new ApiException(ErrorCode.SLOT_NOT_FOUND, "Slot not found");
		}
		if (slot.isBooked()) {
			throw new ApiException(ErrorCode.SLOT_ALREADY_BOOKED, "Cannot delete a booked slot");
		}
		em.remove(slot);
	}

	// SLOT BOOKING

	@Transactional
	public void bookSlot(Long slotId, Long patientId) {
		DoctorSlot slot = em.find(DoctorSlot.class, slotId);
		if (slot == null) {
			throw new ApiException(ErrorCode.SLOT_NOT_FOUND, "Slot not found");
		}

		if (slot.isBooked()) {
			throw new ApiException(ErrorCode.SLOT_ALREADY_BOOKED, "Slot is already booked");
		}

		Patient patient = em.find(Patient.class, patientId);
		if (patient == null) {
			throw new ApiException(ErrorCode.PATIENT_NOT_FOUND, "Patient not found");
		}

		slot.setBooked(true);
		slot.setBookedBy(patient);
		em.merge(slot);
	}

	// SLOT CANCELING
	@Transactional
	public void cancelSlot(Long slotId) {
		// Fetch slot
		DoctorSlot slot = em.find(DoctorSlot.class, slotId);
		if (slot == null) {
			throw new ApiException(ErrorCode.SLOT_NOT_FOUND, "Slot not found");
		}

		// Cancel booking
		slot.setBooked(false);
		slot.setBookedBy(null);
		em.merge(slot);
	}

	// SLOT FETCHING

	public List<SlotViewDTO> getMySlots(String doctorEmail) {
		Doctor doctor = em.createQuery("SELECT d FROM Doctor d WHERE d.email = :email", Doctor.class)
				.setParameter("email", doctorEmail).getResultStream().findFirst()
				.orElseThrow(() -> new ApiException(ErrorCode.DOCTOR_NOT_FOUND, "Doctor not found"));

		List<DoctorSlot> slots = em
				.createQuery("SELECT s FROM DoctorSlot s WHERE s.doctor = :doctor ORDER BY s.startTime",
						DoctorSlot.class)
				.setParameter("doctor", doctor).getResultList();

		return slots.stream().map(SlotAdapter::toViewDTO).collect(Collectors.toList());
	}

	public List<SlotViewDTO> getSlotsForDoctor(Long doctorId) {
		Doctor doctor = em.find(Doctor.class, doctorId);
		if (doctor == null) {
			throw new ApiException(ErrorCode.DOCTOR_NOT_FOUND, "Doctor not found");
		}

		List<DoctorSlot> slots = em
				.createQuery("SELECT s FROM DoctorSlot s WHERE s.doctor.id = :doctorId", DoctorSlot.class)
				.setParameter("doctorId", doctorId).getResultList();

		return slots.stream().map(SlotAdapter::toViewDTO).collect(Collectors.toList());
	}

	public List<SlotViewDTO> getAvailableSlots(Long doctorId) {
		List<DoctorSlot> slots = em
				.createQuery("SELECT s FROM DoctorSlot s WHERE s.doctor.id = :doctorId AND s.booked = false",
						DoctorSlot.class)
				.setParameter("doctorId", doctorId).getResultList();

		return slots.stream().map(SlotAdapter::toViewDTO).collect(Collectors.toList());
	}

	public List<SlotBookedDTO> getBookedSlotsForPatient(String email) {
		List<DoctorSlot> slots = em
				.createQuery("SELECT s FROM DoctorSlot s WHERE s.booked = true AND s.bookedBy.email = :email",
						DoctorSlot.class)
				.setParameter("email", email).getResultList();

		return slots.stream().map(SlotAdapter::toBookedDTO).collect(Collectors.toList());
	}
}
