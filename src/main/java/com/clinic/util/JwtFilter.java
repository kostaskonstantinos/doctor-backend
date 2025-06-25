package com.clinic.util;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		//it can be deleted cause it is also checked on corsfilter that runs first
	    if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) return;

	    String path = requestContext.getUriInfo().getPath();
	    if (path.endsWith("login") || path.endsWith("patients/register")) return;

	    String authHeader = requestContext.getHeaderString("Authorization");
	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	        requestContext.abortWith(
	            Response.status(Response.Status.UNAUTHORIZED)
	                    .entity("{\"error\":\"Missing or invalid Authorization header\"}")
	                    .build()
	        );
	        return;
	    }

        String token = authHeader.substring("Bearer ".length());

        try {
            Claims claims = JwtUtil.validateToken(token);
            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            final SecurityContext originalContext = requestContext.getSecurityContext();// in case https

            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> email;
                }

                @Override
                public boolean isUserInRole(String r) {
                    return role.equalsIgnoreCase(r);
                }

                @Override
                public boolean isSecure() {
                    return originalContext.isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return "Bearer";
                }
            });

        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}

