package com.clinic.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
//ενεργοποιεί το JAX-RS (REST API
@ApplicationPath("/api")// βασικό URI path
public class ClinicApplication extends Application {
}
