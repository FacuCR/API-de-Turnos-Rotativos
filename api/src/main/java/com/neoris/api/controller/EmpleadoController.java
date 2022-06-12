package com.neoris.api.controller;

import com.neoris.api.entity.Empleado;
import com.neoris.api.payload.response.MessageResponse;
import com.neoris.api.service.IEmpleadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {
    @Autowired
    private IEmpleadoService empleadoService;
    private static final Logger logger = LoggerFactory.getLogger(EmpleadoController.class);

    @PostMapping("/save/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> saveEmpleado(@Valid @RequestBody Empleado empleado, @PathVariable("id") Long idUsuario) {
        try {
            empleadoService.saveEmpleado(empleado, idUsuario);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("Los datos del empleado se guardaron con exito!"));
        } catch (Exception e) {
            logger.error("Error: No se pudo guardar los datos del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED)
                    .body(new MessageResponse("Error: Ups ucurrio algo al intentar guardar los datos del empleado!"));
        }
    }

    @GetMapping("/get")
    public ResponseEntity<List<Empleado>> getAllEmpleados() {
        List<Empleado> empleados = empleadoService.getAllEmpleados();
        return new ResponseEntity<>(empleados, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getEmpleadoById(@PathVariable("id") Long idUsuario) {
        try {
            Empleado empleado = empleadoService.getEmpleadoById(idUsuario);
            return new ResponseEntity<>(empleado, HttpStatus.OK);
        } catch(Exception e) {
            logger.error("Error: No se pudo obtener los datos del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: Ups ucurrio algo al intentar obtener los datos del empleado!"));
        }
    }

}
