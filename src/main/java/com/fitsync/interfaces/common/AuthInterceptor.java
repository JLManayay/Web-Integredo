package com.fitsync.interfaces.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitsync.application.identity.AuthQueryHandler;
import com.fitsync.application.identity.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.Optional;

/** Toda la API (salvo registro e inicio de sesión) exige "Authorization: Bearer <token>". */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    static final String ATTR = "fitsync.currentUser";

    private final AuthQueryHandler auth;
    private final ObjectMapper mapper;

    public AuthInterceptor(AuthQueryHandler auth, ObjectMapper mapper) {
        this.auth = auth;
        this.mapper = mapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional<CurrentUser> user = auth.currentUser(tokenOf(request));
        if (user.isPresent()) {
            request.setAttribute(ATTR, user.get());
            return true;
        }
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), new ErrorResponse(Instant.now(), status.value(),
            status.getReasonPhrase(), "Inicia sesión para continuar.", request.getRequestURI()));
        return false;
    }

    static String tokenOf(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header != null && header.startsWith("Bearer ") ? header.substring(7).trim() : null;
    }
}
