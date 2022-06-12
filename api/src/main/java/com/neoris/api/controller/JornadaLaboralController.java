package com.neoris.api.controller;

import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.model.Turno;
import com.neoris.api.payload.request.TurnoNormalRequest;
import com.neoris.api.payload.response.MessageResponse;
import com.neoris.api.service.IControladorDeSemanas;
import com.neoris.api.service.ITurnoNormalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/jornada")
public class JornadaLaboralController {
    @Autowired
    private ITurnoNormalService turnoNormalService;
    @Autowired
    private IControladorDeSemanas controladorDeSemanas;
    private static final Logger logger = LoggerFactory.getLogger(JornadaLaboralController.class);

    @PostMapping("/save/normal/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> saveTurnoNormal(@Valid @RequestBody TurnoNormalRequest turnoNormal, @PathVariable("id") Long jornadaId) {
        try {
            TurnoNormal castTurnoNormal = new TurnoNormal();
            castTurnoNormal.setTurno(turnoNormal.getTurno());
            castTurnoNormal.setFecha(turnoNormal.getFecha());
            castTurnoNormal.setCantHoras(turnoNormal.getCantHoras());
            turnoNormalService.saveTurnoNormal(jornadaId, castTurnoNormal);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("Los datos del turno normal se guardaron con exito!"));
        } catch (Exception e) {
            logger.error("Error: No se pudo guardar los datos del turno normal! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar guardar los datos del turno normal!"));
        }
    }

    @PutMapping("/save/normal/{idJornada}/{idTurnoNormal}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateTurnoNormal(@Valid @RequestBody TurnoNormalRequest turnoNormal, @PathVariable("idJornada") Long jornadaId, @PathVariable("idTurnoNormal") Long turnoNormalId) {
        try {
            TurnoNormal castTurnoNormal = turnoNormalService.getTurnoById(turnoNormalId).get();
            castTurnoNormal.setTurno(turnoNormal.getTurno());
            castTurnoNormal.setFecha(turnoNormal.getFecha());
            castTurnoNormal.setCantHoras(turnoNormal.getCantHoras());
            turnoNormalService.saveTurnoNormal(jornadaId, castTurnoNormal);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("Los datos del turno normal se guardaron con exito!"));
        } catch (Exception e) {
            logger.error("Error: No se pudo guardar los datos del turno normal! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar guardar los datos del turno normal!"));
        }
    }

    @GetMapping("/get/normal/all/{id}")
    public ResponseEntity<?> getAllTurnosNormales(@PathVariable("id") Long idJornada){
        try {
            List<TurnoNormal> turnosNormales = turnoNormalService.getAllTurnosNormales(idJornada);
            return new ResponseEntity<>(turnosNormales, HttpStatus.OK);
        } catch(Exception e) {
            logger.error("Error: No se pudo obtener los datos del empleado! {}", e);
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Error: Ups ucurrio algo al intentar obtener los turnos normales del empleado!"));
        }
    }

    @GetMapping("/get/normal/{id}")
    public ResponseEntity<?> getTurnoNormal(@PathVariable("id") Long idTurnoNormal){
        try {
            TurnoNormal turnoNormal = turnoNormalService.getTurnoById(idTurnoNormal).get();
            return new ResponseEntity<>(turnoNormal, HttpStatus.OK);
        } catch(Exception e) {
            logger.error("Error: No se pudo obtener los datos del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: Ups ucurrio algo al intentar obtener los turnos normales del empleado!"));
        }
    }
}
