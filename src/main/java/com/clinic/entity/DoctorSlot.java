// package com.clinic.entity;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//import com.fasterxml.jackson.annotation.JsonFormat;
//@Entity
//public class DoctorSlot {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    private Doctor doctor;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
//    private LocalDateTime startTime;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
//    private LocalDateTime endTime;
//    private boolean booked;
//
//    @ManyToOne
//    private Patient bookedBy;
//
//    public DoctorSlot() {
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public Doctor getDoctor() {
//        return doctor;
//    }
//
//    public void setDoctor(Doctor doctor) {
//        this.doctor = doctor;
//    }
//
//    public LocalDateTime getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(LocalDateTime startTime) {
//        this.startTime = startTime;
//    }
//
//    public LocalDateTime getEndTime() {
//        return endTime;
//    }
//
//    public void setEndTime(LocalDateTime endTime) {
//        this.endTime = endTime;
//    }
//
//    public boolean isBooked() {
//        return booked;
//    }
//
//    public void setBooked(boolean booked) {
//        this.booked = booked;
//    }
//
//    public Patient getBookedBy() {
//        return bookedBy;
//    }
//
//    public void setBookedBy(Patient bookedBy) {
//        this.bookedBy = bookedBy;
//    }
//}
package com.clinic.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
public class DoctorSlot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Doctor doctor;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime startTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime endTime;

	private boolean booked;

	@ManyToOne
	private Patient bookedBy;

	public DoctorSlot() {
	}

	private DoctorSlot(Builder builder) {
		this.doctor = builder.doctor;
		this.startTime = builder.startTime;
		this.endTime = builder.endTime;
		this.booked = builder.booked;
		this.bookedBy = builder.bookedBy;
	}

	// Getters and Setters

	public Long getId() {
		return id;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public boolean isBooked() {
		return booked;
	}

	public void setBooked(boolean booked) {
		this.booked = booked;
	}

	public Patient getBookedBy() {
		return bookedBy;
	}

	public void setBookedBy(Patient bookedBy) {
		this.bookedBy = bookedBy;
	}

	// Builder class
	public static class Builder {
		private Doctor doctor;
		private LocalDateTime startTime;
		private LocalDateTime endTime;
		private boolean booked = false; // default
		private Patient bookedBy;

		public Builder doctor(Doctor doctor) {
			this.doctor = doctor;
			return this;
		}

		public Builder startTime(LocalDateTime startTime) {
			this.startTime = startTime;
			return this;
		}

		public Builder endTime(LocalDateTime endTime) {
			this.endTime = endTime;
			return this;
		}

		public Builder booked(boolean booked) {
			this.booked = booked;
			return this;
		}

		public Builder bookedBy(Patient bookedBy) {
			this.bookedBy = bookedBy;
			return this;
		}

		public DoctorSlot build() {
			return new DoctorSlot(this);
		}
	}
}
