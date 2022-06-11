package com.neoris.api.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// implemento la interfaz AuthenticationEntryPoint.
// Luego Sobreescribo el método commence().
// Este método se activará cada vez que un usuario no autenticado solicite un recurso HTTP que requiera autorizacion
// y se lanza un AuthenticationException.
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Error no autorizado: {}", authException.getMessage());
        // HttpServletResponse.SC_UNAUTHORIZED es el 401 Status code.
        // Indica que la solicitud requiere autenticación HTTP.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Sin autorización");
    }
}
