package com.neoris.api.controller;

import com.neoris.api.entity.JornadaLaboral;
import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.model.Turno;
import com.neoris.api.payload.request.TurnoNormalRequest;
import com.neoris.api.payload.response.MessageResponse;
import com.neoris.api.repository.JornadaLaboralRepository;
import com.neoris.api.service.IControladorDeSemanas;
import com.neoris.api.service.ITurnoNormalService;
import com.neoris.api.service.ITurnosService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
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
    @Autowired
    private ITurnosService turnosService;
    @Autowired
    private JornadaLaboralRepository jornadaLaboralRepository;
    private static final Logger logger = LoggerFactory.getLogger(JornadaLaboralController.class);
    private final int cantMaxHsDeJornadaSemanal = 48;

    @PostMapping("/save/normal/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> saveTurnoNormal(@Valid @RequestBody TurnoNormalRequest turnoNormalRequest, @PathVariable("id") Long jornadaId) {
        // Debo castear los turnos normales a turno ya que el service controlador de semanas solo
        // trabaja con el tipo Turno

        // Obtengo todos los turnos normales de la jornada del usuario para castearlos a Turno
        List<TurnoNormal> turnosActuales = turnoNormalService.getAllTurnosNormales(jornadaId);
        List<Turno> castTurnosActuales = turnosService.casteoDeTurnosNormales(turnosActuales);

        // Obtengo los demas idJornada para obtener los turnos de los otros usuarios
        // para posteriormente chequear que no hayan dos turnos ocupados por otros usuarios ese mismo dia
        List<Long> todosLosIdsDeLasDemasJornadas = jornadaLaboralRepository.findAll().stream()
                .map(JornadaLaboral::getIdJornada)
                .filter(ids -> !(ids.equals(jornadaId)))
                .collect(Collectors.toList());
        List<TurnoNormal> turnosDeLosDemasUsuarios = new ArrayList<>();
        for (Long idDeOtraJornada : todosLosIdsDeLasDemasJornadas) {
            turnosDeLosDemasUsuarios.addAll(turnoNormalService.getAllTurnosNormales(idDeOtraJornada));
        }
        List<Turno> castTurnosActualesDeLosDemasUsuarios = turnosService.casteoDeTurnosNormales(turnosDeLosDemasUsuarios);

        // Casteo el turno normal nuevo a Turno
        Turno turnoNuevo = turnosService.casteoDeTurnoNormal(turnoNormalRequest);

        // Controlo los requisitos para guardar el turno desde la clase TurnoService por que sino me quedaba mucho codigo duplicado
        ResponseEntity<MessageResponse> controlarRequisitosDelTurno = turnosService.controlarRequsitosDelTurno(castTurnosActuales, castTurnosActualesDeLosDemasUsuarios, turnoNuevo, cantMaxHsDeJornadaSemanal);
        if (controlarRequisitosDelTurno.getStatusCode().equals(HttpStatus.OK)){
            try {
                int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(castTurnosActuales, turnoNuevo) + turnoNuevo.getCantHoras();
                TurnoNormal castTurnoNormal = turnosService.casteoDeRequestATurnoNormal(turnoNormalRequest);
                turnoNormalService.saveTurnoNormal(jornadaId, castTurnoNormal);
                String mensajeResponse = "Los datos del turno normal se guardaron con exito!";
                if (cantidadDeHorasQueQuedarian < 30) {
                    mensajeResponse += " Aun necesita cargar mas hs para llegar a las 30hs minimas de esa semana";
                }
                return ResponseEntity
                        .ok()
                        .body(new MessageResponse(mensajeResponse));
            } catch (Exception e) {
                logger.error("Error: No se pudo guardar los datos del turno normal! {}", e);
                return ResponseEntity
                        .status(HttpStatus.EXPECTATION_FAILED)
                        .body(new MessageResponse("Error: Ups ocurrio algo al intentar guardar los datos del turno normal!"));
            }
        } else {
            return  controlarRequisitosDelTurno;
        }


    }

    @PutMapping("/save/normal/{idJornada}/{idTurnoNormal}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateTurnoNormal(@Valid @RequestBody TurnoNormalRequest turnoNormalRequest, @PathVariable("idJornada") Long jornadaId, @PathVariable("idTurnoNormal") Long turnoNormalId) {
        // Debo castear los turnos normales a turno ya que el service controlador de semanas solo
        // trabaja con el tipo Turno

        // Obtengo todos los turnos normales para castearlos a Turno
        List<TurnoNormal> turnosActuales = turnoNormalService.getAllTurnosNormales(jornadaId);
        List<Turno> castTurnosActuales = turnosService.casteoDeTurnosNormales(turnosActuales);

        // Obtengo los demas idJornada para obtener los turnos de los otros usuarios
        // para posteriormente chequear que no hayan dos turnos ocupados por otros usuarios ese mismo dia
        List<Long> todosLosIdsDeLasDemasJornadas = jornadaLaboralRepository.findAll().stream()
                .map(JornadaLaboral::getIdJornada)
                .filter(ids -> !(ids.equals(jornadaId)))
                .collect(Collectors.toList());
        List<TurnoNormal> turnosDeLosDemasUsuarios = new ArrayList<>();
        for (Long idDeOtraJornada : todosLosIdsDeLasDemasJornadas) {
            turnosDeLosDemasUsuarios.addAll(turnoNormalService.getAllTurnosNormales(idDeOtraJornada));
        }
        List<Turno> castTurnosActualesDeLosDemasUsuarios = turnosService.casteoDeTurnosNormales(turnosDeLosDemasUsuarios);

        // Casteo el turno normal nuevo a Turno
        Turno turnoNuevo = turnosService.casteoDeTurnoNormal(turnoNormalRequest);

        // Controlo los requisitos para guardar el turno desde la clase TurnoService por que sino me quedaba mucho codigo duplicado
        ResponseEntity<MessageResponse> controlarRequisitosDelTurno = turnosService.controlarRequsitosDelTurno(castTurnosActuales, castTurnosActualesDeLosDemasUsuarios, turnoNuevo, cantMaxHsDeJornadaSemanal);
        if (controlarRequisitosDelTurno.getStatusCode().equals(HttpStatus.OK)){
            try {
                int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(castTurnosActuales, turnoNuevo) + turnoNuevo.getCantHoras();
                TurnoNormal castTurnoNormal = turnosService.casteoDeRequestATurnoNormal(turnoNormalRequest);
                castTurnoNormal.setIdTurnoNormal(turnoNormalId);
                turnoNormalService.saveTurnoNormal(jornadaId, castTurnoNormal);
                String mensajeResponse = "Los datos del turno normal se guardaron con exito!";
                if (cantidadDeHorasQueQuedarian < 30) {
                    mensajeResponse += " Aun necesita cargar mas hs para llegar a las 30hs minimas de esa semana";
                }
                return ResponseEntity
                        .ok()
                        .body(new MessageResponse(mensajeResponse));
            } catch (Exception e) {
                logger.error("Error: No se pudo guardar los datos del turno normal! {}", e);
                return ResponseEntity
                        .status(HttpStatus.EXPECTATION_FAILED)
                        .body(new MessageResponse("Error: Ups ocurrio algo al intentar guardar los datos del turno normal!"));
            }
        } else {
            return  controlarRequisitosDelTurno;
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
