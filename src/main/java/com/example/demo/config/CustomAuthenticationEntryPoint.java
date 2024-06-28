package com.example.demo.config;


import com.example.demo.payload.ErrorLoad;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();
    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        this.delegate.commence(request,response,authException);
        response.setContentType("application/json; charset=UTF-8");

        ErrorLoad errorLoad = new ErrorLoad();
        errorLoad.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        String errorMsg = Optional.ofNullable(authException.getCause()).map(Throwable::getMessage).orElse(authException.getMessage());
        errorLoad.setError(errorMsg);
        errorLoad.setMessage("Token không hợp lệ");
        mapper.writeValue(response.getWriter(),errorLoad);
    }
}
