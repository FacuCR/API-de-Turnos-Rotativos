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
import java.util.Iterator;
import java.util.List;

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
        int cantMaxHsDeJornadaSemanal = 48;
        // Debo castear los turnos normales a turno ya que el service controlador de semanas solo
        // trabaja con el tipo Turno

        // Obtengo todos los turnos normales para castearlos a Turno
        List<TurnoNormal> turnosActuales = turnoNormalService.getAllTurnosNormales(jornadaId);
        List<Turno> castTurnosActuales = new ArrayList<>();
        Iterator<TurnoNormal> turnosActualesiT = turnosActuales.iterator();
        while (turnosActualesiT.hasNext()) {
            TurnoNormal turnoNext = turnosActualesiT.next();
            Turno turno = new Turno();
            turno.setTurno(turnoNext.getTurno());
            turno.setFecha(turnoNext.getFecha());
            turno.setCantHoras(turnoNext.getCantHoras());
            castTurnosActuales.add(turno);
        }
        // Casteo el turno normal nuevo a Turno
        Turno turnoNuevo = new Turno();
        turnoNuevo.setTurno(turnoNormal.getTurno());
        turnoNuevo.setFecha(turnoNormal.getFecha());
        turnoNuevo.setCantHoras(turnoNormal.getCantHoras());


        int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(castTurnosActuales, turnoNuevo) + turnoNuevo.getCantHoras();
        // Controlo que la suma de la jornada laboral de esa semana mas el nuevo turno no supere las 48hs
        if (cantidadDeHorasQueQuedarian <= cantMaxHsDeJornadaSemanal) {
            // Controlo que no se guarda en la misma jornada laboral el mismo turno
            if (controladorDeSemanas.isElMismoUsuarioEnElMismoTurno(castTurnosActuales, turnoNuevo)) {
                // Controlo que no se guarde si ya hay dos turnos ocupados en ese dia
                if (controladorDeSemanas.isTurnoOcupado(castTurnosActuales, turnoNuevo)) {
                    try {
                        TurnoNormal castTurnoNormal = new TurnoNormal();
                        castTurnoNormal.setTurno(turnoNormal.getTurno());
                        castTurnoNormal.setFecha(turnoNormal.getFecha());
                        castTurnoNormal.setCantHoras(turnoNormal.getCantHoras());
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
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(new MessageResponse("Error: No se pudo guardar los datos del turno normal por que los " + turnoNuevo.getTurno() + " de ese dia estan ocupados!"));
                }
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("Error: No se pudo guardar los datos del turno normal por que ya tienes un " + turnoNuevo.getTurno() + " asignado ese dia!"));
            }
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar los datos del turno normal por que supera el limite de horas(48hs semanales)!"));
        }
    }

    @PutMapping("/save/normal/{idJornada}/{idTurnoNormal}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateTurnoNormal(@Valid @RequestBody TurnoNormalRequest turnoNormal, @PathVariable("idJornada") Long jornadaId, @PathVariable("idTurnoNormal") Long turnoNormalId) {
        int cantMaxHsDeJornadaSemanal = 48;
        // Debo castear los turnos normales a turno ya que el service controlador de semanas solo
        // trabaja con el tipo Turno

        // Obtengo todos los turnos normales para castearlos a Turno
        List<TurnoNormal> turnosActuales = turnoNormalService.getAllTurnosNormales(jornadaId);
        List<Turno> castTurnosActuales = new ArrayList<>();
        Iterator<TurnoNormal> turnosActualesiT = turnosActuales.iterator();
        while (turnosActualesiT.hasNext()) {
            TurnoNormal turnoNext = turnosActualesiT.next();
            Turno turno = new Turno();
            turno.setTurno(turnoNext.getTurno());
            turno.setFecha(turnoNext.getFecha());
            turno.setCantHoras(turnoNext.getCantHoras());
            castTurnosActuales.add(turno);
        }
        // Casteo el turno normal nuevo a Turno
        Turno turnoNuevo = new Turno();
        turnoNuevo.setTurno(turnoNormal.getTurno());
        turnoNuevo.setFecha(turnoNormal.getFecha());
        turnoNuevo.setCantHoras(turnoNormal.getCantHoras());


        int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(castTurnosActuales, turnoNuevo) + turnoNuevo.getCantHoras();
        // Controlo que la suma de la jornada laboral de esa semana mas el nuevo turno no supere las 48hs
        if (cantidadDeHorasQueQuedarian <= cantMaxHsDeJornadaSemanal) {
            // Controlo que no se guarda en la misma jornada laboral el mismo turno
            if (controladorDeSemanas.isElMismoUsuarioEnElMismoTurno(castTurnosActuales, turnoNuevo)) {
                // Controlo que no se guarde si ya hay dos turnos ocupados en ese dia
                if (controladorDeSemanas.isTurnoOcupado(castTurnosActuales, turnoNuevo)) {
                    try {
                        TurnoNormal castTurnoNormal = turnoNormalService.getTurnoById(turnoNormalId).get();
                        castTurnoNormal.setTurno(turnoNormal.getTurno());
                        castTurnoNormal.setFecha(turnoNormal.getFecha());
                        castTurnoNormal.setCantHoras(turnoNormal.getCantHoras());
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
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body(new MessageResponse("Error: No se pudo guardar los datos del turno normal por que los " + turnoNuevo.getTurno() + " de ese dia estan ocupados!"));
                }
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("Error: No se pudo guardar los datos del turno normal por que ya tienes un " + turnoNuevo.getTurno() + " asignado ese dia!"));
            }
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar los datos del turno normal por que supera el limite de horas(48hs semanales)!"));
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
