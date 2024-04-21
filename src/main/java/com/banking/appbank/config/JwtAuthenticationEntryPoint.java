package com.banking.appbank.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    //La función principal de esta clase es manejar las solicitudes no autenticadas,
    // enviando un código de estado HTTP 401 (Unauthorized) junto con un mensaje de error.

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
       response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
    
}
