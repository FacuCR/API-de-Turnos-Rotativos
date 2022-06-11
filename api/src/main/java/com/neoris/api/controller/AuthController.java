package com.neoris.api.controller;

import com.neoris.api.entity.Role;
import com.neoris.api.entity.Usuario;
import com.neoris.api.enums.ERole;
import com.neoris.api.payload.request.LoginRequest;
import com.neoris.api.payload.request.SignupRequest;
import com.neoris.api.payload.response.JwtResponse;
import com.neoris.api.payload.response.MessageResponse;
import com.neoris.api.repository.RoleRepository;
import com.neoris.api.repository.UsuarioRepository;
import com.neoris.api.security.jwt.AuthTokenFilter;
import com.neoris.api.security.jwt.JwtUtils;
import com.neoris.api.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtils jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // autenticar { nombre de usuario, contraseña }
    // actualizar SecurityContext utilizando el objeto Authentication
    // generar el JWT
    // obtener UserDetails del objeto Authentication
    // la respuesta contiene los datos del JWT y UserDetails
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    roles));
        } catch (Exception e) {
            logger.error("No se puede establecer la autenticación del usuario: {}", e);
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El usuairo o la contraseña son incorrectos!"));
        }
    }

    // Comprobar si el nombre de usuario existe,
    // crear un nuevo usuario (con ROLE_USER si no se especifica el rol) y
    // guardar el usuario en la base de datos usando UserRepository.
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Este Username ya esta ocupado!"));
        }
        // Crear el nuevo usuario
        Usuario user = new Usuario(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()));
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: El rol no se encuentra."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: El rol no se encuentra."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: El rol no se encuentra."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        usuarioRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Usuario registrado con éxito  !"));
    }
}
