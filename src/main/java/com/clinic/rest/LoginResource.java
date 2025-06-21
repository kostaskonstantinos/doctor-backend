//package com.clinic.rest;
//
//import java.util.logging.Logger;
//
//import org.mindrot.jbcrypt.BCrypt;
//
//import com.clinic.dto.LoginResponse;
//import com.clinic.dto.UserCredentials;
//import com.clinic.entity.Admin;
//import com.clinic.entity.Doctor;
//import com.clinic.entity.Patient;
//import com.clinic.util.JwtUtil;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.transaction.Transactional;
//import jakarta.ws.rs.Consumes;
//import jakarta.ws.rs.POST;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.Produces;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//
//@Path("/login")
//@Consumes(MediaType.APPLICATION_JSON)
//@Produces(MediaType.APPLICATION_JSON)
//public class LoginResource {
//
//    @PersistenceContext(unitName = "ClinicPU")
//    private EntityManager em;
//    // to inject test
//    public void setEntityManager(EntityManager em) {
//        this.em = em;
//    }
//    private static final Logger logger = Logger.getLogger(LoginResource.class.getName());
//
//    @POST
//    @Transactional
//    public Response login(UserCredentials credentials) {
//        try {
//            logger.info("Login attempt for email: " + credentials.getEmail());
//            logger.info("Password received: " + credentials.getPassword());
//
//            // Check Admin
//            Admin admin = em.createQuery("SELECT a FROM Admin a WHERE a.email = :email", Admin.class)
//            	    .setParameter("email", credentials.getEmail())
//            	    .getResultStream().findFirst().orElse(null);
//
//            	if (admin != null && BCrypt.checkpw(credentials.getPassword(), admin.getPassword())) {
//            	    String token = JwtUtil.generateToken(admin.getEmail(), "admin");
//            	    return Response.ok(new LoginResponse(token, "admin")).build();
//            	}
//
//            logger.info("Admin query result: " + (admin != null ? admin.getEmail() : "null"));
//
//           
//            // Check Doctor
//            Doctor doctor = em.createQuery("SELECT d FROM Doctor d WHERE d.email = :email", Doctor.class)
//            	    .setParameter("email", credentials.getEmail())
//            	    .getResultStream().findFirst().orElse(null);
//
//            	if (doctor != null && BCrypt.checkpw(credentials.getPassword(), doctor.getPassword())) {
//            	    String token = JwtUtil.generateToken(doctor.getEmail(), "doctor");
//            	    return Response.ok(new LoginResponse(token, "doctor")).build();
//            	}
//            logger.info("Doctor query result: " + (doctor != null ? doctor.getEmail() : "null"));
//
//         
//            // Check Patient
//            Patient patient = em.createQuery("SELECT p FROM Patient p WHERE p.email = :email", Patient.class)
//                    .setParameter("email", credentials.getEmail())
//                   // .setParameter("password", credentials.getPassword())
//                    .getResultStream().findFirst().orElse(null);
//            if (patient != null && BCrypt.checkpw(credentials.getPassword(), patient.getPassword())) {
//                String token = JwtUtil.generateToken(patient.getEmail(), "patient");
//                return Response.ok(new LoginResponse(token, "patient")).build();
//            }
//            logger.info("Patient query result: " + (patient != null ? patient.getEmail() : "null"));
//
////            if (patient != null) {
////                String token = JwtUtil.generateToken(patient.getEmail(), "patient");
////                return Response.ok(new LoginResponse(token, "patient")).build();
////            }
//
//            logger.warning("No user matched.");
//            return Response.status(Response.Status.UNAUTHORIZED)
//                    .entity("{\"error\":\"Invalid credentials\"}").build();
//
//        } catch (Exception e) {
//            logger.severe("Login error: " + e.getMessage());
//            e.printStackTrace();
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity("{\"error\":\"Server error\"}").build();
//        }
//    }
//}
package com.clinic.rest;

import com.clinic.dto.LoginResponse;
import com.clinic.dto.UserCredentials;
import com.clinic.exception.ApiException;
import com.clinic.exception.ErrorCode;
import com.clinic.service.LoginService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

	@Inject
	private LoginService loginService;

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

	@POST
	@Transactional
	public Response login(@Valid UserCredentials credentials) {
		try {
			LoginResponse response = loginService.authenticate(credentials);

			if (response != null) {
				return Response.ok(response).build();
			} else {
				throw new ApiException(ErrorCode.INVALID_CREDENTIALS, "Invalid email or password");
			}
		} catch (ApiException ex) {
			throw ex; // Preserve original error code and message
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApiException(ErrorCode.SERVER_ERROR, "Login service error" + e.getMessage());
		}
	}

}
