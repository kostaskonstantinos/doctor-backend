//package com.clinic.entity;
//
//import jakarta.persistence.*;
//
//@Entity
//public class Doctor {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String name;
//    private String specialization;
//    private String email;
//    private String password;    
//    private String phoneNumber;    
//    private String address;    
//    private String workingHours;
//    
//    // Constructors
//    public Doctor() {}
//
//    public Doctor(String name, String specialization, String email, String password, String phoneNumber,String address,String workingHours) {
//        this.name = name;
//        this.specialization = specialization;
//        this.email = email;
//        this.password = password;
//        this.phoneNumber = phoneNumber;
//        this.address = address;
//        this.workingHours = workingHours;
//    }
//    // Getters and Setters
//
//    public Long getId() {
//        return id;
//    }
//    public String getName() {
//        return name;
//    }
//    public void setName(String name) {
//        this.name = name;
//    }
//    public String getSpecialization() {
//        return specialization;
//    }
//    public void setSpecialization(String specialization) {
//        this.specialization = specialization;
//    }
//    public String getEmail() {
//        return email;
//    }
//    public void setEmail(String email) {
//        this.email = email;
//    }
//    public String getPassword() {
//        return password;
//    }
//    public String getPhoneNumber() { 
//    	return phoneNumber; 
//    	}
//    public void setPhoneNumber(String phoneNumber) {
//    	this.phoneNumber = phoneNumber; 
//    	}
//
//    public String getAddress() { 
//    	return address; 
//    	}
//    public void setAddress(String address) {
//    	this.address = address; 
//    	}
//
//    public String getWorkingHours() {
//    	return workingHours; 
//    	}
//    public void setWorkingHours(String workingHours) { 
//    	this.workingHours = workingHours; 
//    	}
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//}
package com.clinic.entity;

import jakarta.persistence.*;

@Entity
public class Doctor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String specialization;
	private String email;
	private String password;
	private String phoneNumber;
	private String address;
	private String workingHours;

	public Doctor() {
	}

	private Doctor(Builder builder) {
		this.name = builder.name;
		this.specialization = builder.specialization;
		this.email = builder.email;
		this.password = builder.password;
		this.phoneNumber = builder.phoneNumber;
		this.address = builder.address;
		this.workingHours = builder.workingHours;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSpecialization() {
		return specialization;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public String getWorkingHours() {
		return workingHours;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}

	// Builder class
	public static class Builder {
		private String name;
		private String specialization;
		private String email;
		private String password;
		private String phoneNumber;
		private String address;
		private String workingHours;

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder specialization(String specialization) {
			this.specialization = specialization;
			return this;
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder password(String password) {
			this.password = password;
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

		public Builder workingHours(String workingHours) {
			this.workingHours = workingHours;
			return this;
		}

		public Doctor build() {
			return new Doctor(this);
		}
	}
}
