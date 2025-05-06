package com.airbnb.configs.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.airbnb.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * This method is called when an exception is thrown in the filter chain.
     *
     * @param request
     * @param response
     * @param authException
     * @throws IOException
     * @throws ServletException
     */

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
        this.delegate.commence(request, response, authException);

        // Đề phòng trường hợp nếu như response đã được commit (đã được gửi cho client
        // rồi) thì lúc này sẽ không thể ghi đè thêm được nữa. Có thể gây exception
        // IllegalStateException
        if (!response.isCommitted()) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ErrorResponse responseBody = ErrorResponse.builder()
                    .message(authException.getMessage())
                    .path(request.getRequestURI())
                    .build();
            mapper.writeValue(response.getWriter(), responseBody);
        }
    }
}
