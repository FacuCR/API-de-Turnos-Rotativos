package com.neoris.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
// Clase para probar los niveles de acceso
// Hay 3 APIs:
// /api/test/all para el acceso público
// /api/test/user para los usuarios que tienen ROLE_USER o ROLE_ADMIN
// /api/test/admin para usuarios que tienen ROLE_ADMIN
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Contenido Publico.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userAccess() {
        return "Contenido para Usuarios.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Contenido para Admin.";
    }
}
