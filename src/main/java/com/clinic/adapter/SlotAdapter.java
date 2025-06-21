package com.clinic.adapter;

import com.clinic.dto.SlotBookedDTO;
import com.clinic.dto.SlotRequest;
import com.clinic.dto.SlotViewDTO;
import com.clinic.entity.Doctor;
import com.clinic.entity.DoctorSlot;

public class SlotAdapter {

	public static DoctorSlot toEntity(SlotRequest request, Doctor doctor) {
		return new DoctorSlot.Builder().doctor(doctor).startTime(request.getStartTime()).booked(false).build();
	}

	public static SlotViewDTO toViewDTO(DoctorSlot slot) {
		SlotViewDTO dto = new SlotViewDTO();
		dto.setId(slot.getId());
		dto.setStartTime(slot.getStartTime());
		dto.setBooked(slot.isBooked());
		dto.setDoctorName(slot.getDoctor().getName());
		dto.setPatientName(slot.getBookedBy() != null ? slot.getBookedBy().getName() : null);
		return dto;
	}

	public static SlotBookedDTO toBookedDTO(DoctorSlot slot) {
		SlotBookedDTO dto = new SlotBookedDTO();
		dto.setId(slot.getId());
		dto.setStartTime(slot.getStartTime());
		dto.setDoctorName(slot.getDoctor().getName());
		return dto;
	}

}
