package com.neoris.api.security.services.jwt;

import com.neoris.api.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    // Manejo el valor de la palabra secreta y la expiracion del token desde application.properties
    @Value("${facundocastro.app.jwtSecret}")
    private String jwtSecret;
    @Value("${facundocastro.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // generar un JWT a partir de nombre de usuario, fecha, caducidad, secreto
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // obtener el nombre de usuario del JWT
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    // Validar el JWT
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Firma JWT inválida: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Token JWT inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token ha expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token no es compatible: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT afirma que el string esta vacio: {}", e.getMessage());
        }
        return false;
    }
}
