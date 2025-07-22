package com.keodam.keodam_backend.global.security.oidc.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IdTokenLoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("error", "Bad Request");
        errorBody.put("message", exception.getMessage());

        errorBody.put("stackTrace", Arrays.stream(exception.getStackTrace())
                .filter(e -> e.getClassName().startsWith("com.keodam"))
                .map(StackTraceElement::toString)
                .toArray());

        ObjectMapper objectMapper = new ObjectMapper();
        writer.write(objectMapper.writeValueAsString(errorBody));
        response.getWriter().flush();
    }
}
