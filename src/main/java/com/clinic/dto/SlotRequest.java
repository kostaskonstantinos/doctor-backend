//package com.clinic.dto;
//
//public class SlotRequest {
//    private Long doctorId;
//    private String startTime; // ISO string: e.g. 2024-07-01T09:00
//
//    public Long getDoctorId() {
//        return doctorId;
//    }
//
//    public void setDoctorId(Long doctorId) {
//        this.doctorId = doctorId;
//    }
//
//    public String getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(String startTime) {
//        this.startTime = startTime;
//    }
//}
package com.clinic.dto;

import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class SlotRequest {

	@NotNull(message = "{doctorId.required}")
	private Long doctorId;

	@NotNull(message = "{slot.startTime.required}")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") 
	private LocalDateTime startTime;

	public SlotRequest() {
	}

	// Getters and Setters
	public Long getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(Long doctorId) {
		this.doctorId = doctorId;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
}
