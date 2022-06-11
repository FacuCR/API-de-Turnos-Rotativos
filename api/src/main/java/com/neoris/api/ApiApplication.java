package com.neoris.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

	// Al principio intente crear el usuario desde aqui pero como no supe como pasar la preautorizacion de hasRole("ADMIN")
	// para crear el primer admin usuario lo hago manualmente desde /resources/data.sql
	/*
	@Autowired
	private AuthController authController;

	@EventListener(ApplicationReadyEvent.class)
	private void crearPrimerAdminPostAppReadyEvent() {
		SignupRequest signupRequest = new SignupRequest();
		signupRequest.setUsername("admin");
		signupRequest.setPassword("123456");
		Set<String> roles = new HashSet<>();
		roles.add("admin");
		signupRequest.setRole(roles);
		authController.registerUser(signupRequest);
	}*/

}
