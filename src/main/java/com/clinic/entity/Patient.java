//package com.clinic.entity;
//
//import java.io.Serializable;
////import com.fasterxml.jackson.annotation.JsonProperty;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import java.time.LocalDate;
//
//
//
//
//@Entity
//public class Patient implements Serializable{
//    private static final long serialVersionUID=1l;
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String email;
//    private String password;
//    private String name;
//    private String phoneNumber;
//    private String address;
//    private LocalDate dateOfBirth;
//    private String gender;
//    private String medicalNotes;
//    public Patient() {
//    }
//
//    public Patient(String email, String password, String name, String phoneNumber, String address,
//            LocalDate dateOfBirth, String gender) {
// this.email = email;
// this.password = password;
// this.name = name;
// this.phoneNumber = phoneNumber;
// this.address = address;
// this.dateOfBirth = dateOfBirth;
// this.gender = gender;
//}
//
//    public Long getId() {
//        return id;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public LocalDate getDateOfBirth() {
//        return dateOfBirth;
//    }
//
//    public void setDateOfBirth(LocalDate dateOfBirth) {
//        this.dateOfBirth = dateOfBirth;
//    }
//
//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//
//    public String getMedicalNotes() {
//        return medicalNotes;
//    }
//
//    public void setMedicalNotes(String medicalNotes) {
//        this.medicalNotes = medicalNotes;
//    }
//}
package com.clinic.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class Patient implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;
	private String password;
	private String name;
	private String phoneNumber;
	private String address;
	private LocalDate dateOfBirth;
	private String gender;

	public Patient() {
	}

	private Patient(Builder builder) {
		this.email = builder.email;
		this.password = builder.password;
		this.name = builder.name;
		this.phoneNumber = builder.phoneNumber;
		this.address = builder.address;
		this.dateOfBirth = builder.dateOfBirth;
		this.gender = builder.gender;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	// Builder class
	public static class Builder {
		private String email;
		private String password;
		private String name;
		private String phoneNumber;
		private String address;
		private LocalDate dateOfBirth;
		private String gender;

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder phoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
			return this;
		}

		public Builder address(String address) {
			this.address = address;
			return this;
		}

		public Builder dateOfBirth(LocalDate dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
			return this;
		}

		public Builder gender(String gender) {
			this.gender = gender;
			return this;
		}

		public Patient build() {
			return new Patient(this);
		}
	}
}
