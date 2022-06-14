package com.neoris.api.controller;

import com.neoris.api.entity.JornadaLaboral;
import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.model.Turno;
import com.neoris.api.payload.request.TurnoExtraRequest;
import com.neoris.api.payload.request.TurnoNormalRequest;
import com.neoris.api.payload.response.MessageResponse;
import com.neoris.api.repository.JornadaLaboralRepository;
import com.neoris.api.service.IControladorDeSemanas;
import com.neoris.api.service.ITurnoExtraService;
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
    private ITurnoExtraService turnoExtraService;
    @Autowired
    private IControladorDeSemanas controladorDeSemanas;
    @Autowired
    private ITurnosService turnosService;
    @Autowired
    private JornadaLaboralRepository jornadaLaboralRepository;
    private static final Logger logger = LoggerFactory.getLogger(JornadaLaboralController.class);
    private final int cantMaxHsDeJornadaSemanal = 48;

    // ========== TURNOS NORMALES ========== //

    @PostMapping("/save/normal/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> saveTurnoNormal(@Valid @RequestBody TurnoNormalRequest turnoNormalRequest, @PathVariable("id") Long jornadaId) {
        // Debo castear los turnos normales a turno ya que el service controlador de semanas solo
        // trabaja con el tipo Turno

        // Obtengo todos los turnos de la jornada del usuario para castearlos a Turno
        List<TurnoNormal> turnosActuales = turnoNormalService.getAllTurnosNormales(jornadaId);
        List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);
        List<Turno> castTurnosActuales = turnosService.casteoDeTurnosNormales(turnosActuales);
        castTurnosActuales.addAll(turnosService.casteoDeTurnosExtras(turnosExtrasActuales));

        // Obtengo los demas idJornada para obtener los turnos de los otros usuarios
        // para posteriormente chequear que no hayan dos turnos ocupados por otros usuarios ese mismo dia
        List<Long> todosLosIdsDeLasDemasJornadas = jornadaLaboralRepository.findAll().stream()
                .map(JornadaLaboral::getIdJornada)
                .filter(ids -> !(ids.equals(jornadaId)))
                .collect(Collectors.toList());
        List<TurnoNormal> turnosNormalesDeLosDemasUsuarios = new ArrayList<>();
        List<TurnoExtra> turnosExtrasDeLosDemasUsuarios = new ArrayList<>();
        for (Long idDeOtraJornada : todosLosIdsDeLasDemasJornadas) {
            turnosNormalesDeLosDemasUsuarios.addAll(turnoNormalService.getAllTurnosNormales(idDeOtraJornada));
            turnosExtrasDeLosDemasUsuarios.addAll(turnoExtraService.getAllTurnosExtras(idDeOtraJornada));
        }
        List<Turno> castTurnosActualesDeLosDemasUsuarios = turnosService.casteoDeTurnosNormales(turnosNormalesDeLosDemasUsuarios);
        castTurnosActualesDeLosDemasUsuarios.addAll(turnosService.casteoDeTurnosNormales(turnosNormalesDeLosDemasUsuarios));

        // Casteo el turno normal nuevo a Turno
        Turno turnoNuevo = turnosService.casteoDeTurnoNormal(turnoNormalRequest);

        // Controlo los requisitos para guardar el turno desde la clase TurnoService por que sino me quedaba mucho codigo duplicado
        ResponseEntity<MessageResponse> controlarRequisitosDelTurno = turnosService.controlarRequsitosDelTurno(castTurnosActuales, castTurnosActualesDeLosDemasUsuarios, turnoNuevo, new ArrayList<TurnoExtra>(), turnosActuales);
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

        // Obtengo todos los turnos para castearlos a Turno
        List<TurnoNormal> turnosActuales = turnoNormalService.getAllTurnosNormales(jornadaId);
        List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);
        List<Turno> castTurnosActuales = turnosService.casteoDeTurnosNormales(turnosActuales);
        castTurnosActuales.addAll(turnosService.casteoDeTurnosExtras(turnosExtrasActuales));

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
        ResponseEntity<MessageResponse> controlarRequisitosDelTurno = turnosService.controlarRequsitosDelTurno(castTurnosActuales, castTurnosActualesDeLosDemasUsuarios, turnoNuevo, new ArrayList<TurnoExtra>(), turnosActuales);
        if (controlarRequisitosDelTurno.getStatusCode().equals(HttpStatus.OK)){
            try {
                int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(castTurnosActuales, turnoNuevo) + turnoNuevo.getCantHoras();
                TurnoNormal castTurnoNormal = turnosService.casteoDeRequestATurnoNormal(turnoNormalRequest);
                turnoNormalService.saveTurnoNormal(jornadaId, turnoNormalId, castTurnoNormal);
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
            logger.error("Error: No se pudo obtener los turnos normales del empleado! {}", e);
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse("Error: Ups ocurrio algo al intentar obtener los turnos normales del empleado!"));
        }
    }

    @GetMapping("/get/normal/{id}")
    public ResponseEntity<?> getTurnoNormal(@PathVariable("id") Long idTurnoNormal){
        try {
            TurnoNormal turnoNormal = turnoNormalService.getTurnoById(idTurnoNormal).get();
            return new ResponseEntity<>(turnoNormal, HttpStatus.OK);
        } catch(Exception e) {
            logger.error("Error: No se pudo obtener el turno normal del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar obtener los turnos normales del empleado!"));
        }
    }

    @DeleteMapping("/delete/normal/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteTurnoNormal(@PathVariable("id") Long idTurnoNormal){
        try {
            if(turnoNormalService.deleteTurnoNormal(idTurnoNormal))
                return ResponseEntity
                        .ok()
                        .body(new MessageResponse("El turno se elimino correctamente!"));
            else
                return ResponseEntity
                        .status(HttpStatus.NOT_MODIFIED)
                        .body(new MessageResponse("Error: Ups ocurrio algo al intentar borrar el turno normal!"));
        } catch (Exception e) {
            logger.error("Error: No se pudo borrar el turno normal! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Error: Ups ucurrio algo al intentar borrar el turno normal!"));
        }
    }




    // ========== TURNOS EXTRAS ========== //


    @PostMapping("/save/extra/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> saveTurnoExtra(@Valid @RequestBody TurnoExtraRequest turnoExtraRequest, @PathVariable("id") Long jornadaId) {
        // Debo castear los turnos extras a turno ya que el service controlador de semanas solo
        // trabaja con el tipo Turno

        // Obtengo todos los turnos de la jornada del usuario para castearlos a Turno
        List<TurnoNormal> turnosActuales = turnoNormalService.getAllTurnosNormales(jornadaId);
        List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);
        List<Turno> castTurnosActuales = turnosService.casteoDeTurnosNormales(turnosActuales);
        castTurnosActuales.addAll(turnosService.casteoDeTurnosExtras(turnosExtrasActuales));

        // Obtengo los demas idJornada para obtener los turnos de los otros usuarios
        // para posteriormente chequear que no hayan dos turnos ocupados por otros usuarios ese mismo dia
        List<Long> todosLosIdsDeLasDemasJornadas = jornadaLaboralRepository.findAll().stream()
                .map(JornadaLaboral::getIdJornada)
                .filter(ids -> !(ids.equals(jornadaId)))
                .collect(Collectors.toList());
        List<TurnoNormal> turnosNormalesDeLosDemasUsuarios = new ArrayList<>();
        List<TurnoExtra> turnosExtrasDeLosDemasUsuarios = new ArrayList<>();
        for (Long idDeOtraJornada : todosLosIdsDeLasDemasJornadas) {
            turnosNormalesDeLosDemasUsuarios.addAll(turnoNormalService.getAllTurnosNormales(idDeOtraJornada));
            turnosExtrasDeLosDemasUsuarios.addAll(turnoExtraService.getAllTurnosExtras(idDeOtraJornada));
        }
        List<Turno> castTurnosActualesDeLosDemasUsuarios = turnosService.casteoDeTurnosNormales(turnosNormalesDeLosDemasUsuarios);
        castTurnosActualesDeLosDemasUsuarios.addAll(turnosService.casteoDeTurnosNormales(turnosNormalesDeLosDemasUsuarios));

        // Casteo el turno normal nuevo a Turno
        Turno turnoNuevo = turnosService.casteoDeTurnoExtra(turnoExtraRequest);

        // Controlo los requisitos para guardar el turno desde la clase TurnoService por que sino me quedaba mucho codigo duplicado
        ResponseEntity<MessageResponse> controlarRequisitosDelTurno = turnosService.controlarRequsitosDelTurno(castTurnosActuales, castTurnosActualesDeLosDemasUsuarios, turnoNuevo, turnosExtrasActuales, new ArrayList<TurnoNormal>());
        if (controlarRequisitosDelTurno.getStatusCode().equals(HttpStatus.OK)){
            try {
                int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(castTurnosActuales, turnoNuevo) + turnoNuevo.getCantHoras();
                TurnoExtra castTurnoExtra = turnosService.casteoDeRequestATurnoExtra(turnoExtraRequest);
                turnoExtraService.saveTurnoExtra(jornadaId, castTurnoExtra);
                String mensajeResponse = "Los datos del turno extra se guardaron con exito!";
                if (cantidadDeHorasQueQuedarian < 30) {
                    mensajeResponse += " Aun necesita cargar mas hs para llegar a las 30hs minimas de esa semana";
                }
                return ResponseEntity
                        .ok()
                        .body(new MessageResponse(mensajeResponse));
            } catch (Exception e) {
                logger.error("Error: No se pudo guardar los datos del turno extra! {}", e);
                return ResponseEntity
                        .status(HttpStatus.EXPECTATION_FAILED)
                        .body(new MessageResponse("Error: Ups ocurrio algo al intentar guardar los datos del turno extra!"));
            }
        } else {
            return  controlarRequisitosDelTurno;
        }


    }

    @PutMapping("/save/extra/{idJornada}/{idTurnoNormal}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateTurnoExtra(@Valid @RequestBody TurnoExtraRequest turnoExtraRequest, @PathVariable("idJornada") Long jornadaId, @PathVariable("idTurnoNormal") Long turnoExtraId) {
        // Debo castear los turnos extras a turno ya que el service controlador de semanas solo
        // trabaja con el tipo Turno

        // Obtengo todos los turnos de la jornada del usuario para castearlos a Turno
        List<TurnoNormal> turnosActuales = turnoNormalService.getAllTurnosNormales(jornadaId);
        List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);
        List<Turno> castTurnosActuales = turnosService.casteoDeTurnosNormales(turnosActuales);
        castTurnosActuales.addAll(turnosService.casteoDeTurnosExtras(turnosExtrasActuales));

        // Obtengo los demas idJornada para obtener los turnos de los otros usuarios
        // para posteriormente chequear que no hayan dos turnos ocupados por otros usuarios ese mismo dia
        List<Long> todosLosIdsDeLasDemasJornadas = jornadaLaboralRepository.findAll().stream()
                .map(JornadaLaboral::getIdJornada)
                .filter(ids -> !(ids.equals(jornadaId)))
                .collect(Collectors.toList());
        List<TurnoNormal> turnosNormalesDeLosDemasUsuarios = new ArrayList<>();
        List<TurnoExtra> turnosExtrasDeLosDemasUsuarios = new ArrayList<>();
        for (Long idDeOtraJornada : todosLosIdsDeLasDemasJornadas) {
            turnosNormalesDeLosDemasUsuarios.addAll(turnoNormalService.getAllTurnosNormales(idDeOtraJornada));
            turnosExtrasDeLosDemasUsuarios.addAll(turnoExtraService.getAllTurnosExtras(idDeOtraJornada));
        }
        List<Turno> castTurnosActualesDeLosDemasUsuarios = turnosService.casteoDeTurnosNormales(turnosNormalesDeLosDemasUsuarios);
        castTurnosActualesDeLosDemasUsuarios.addAll(turnosService.casteoDeTurnosNormales(turnosNormalesDeLosDemasUsuarios));

        // Casteo el turno normal nuevo a Turno
        Turno turnoNuevo = turnosService.casteoDeTurnoExtra(turnoExtraRequest);

        // Controlo los requisitos para guardar el turno desde la clase TurnoService por que sino me quedaba mucho codigo duplicado
        ResponseEntity<MessageResponse> controlarRequisitosDelTurno = turnosService.controlarRequsitosDelTurno(castTurnosActuales, castTurnosActualesDeLosDemasUsuarios, turnoNuevo, turnosExtrasActuales, new ArrayList<TurnoNormal>());
        if (controlarRequisitosDelTurno.getStatusCode().equals(HttpStatus.OK)){
            try {
                int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(castTurnosActuales, turnoNuevo) + turnoNuevo.getCantHoras();
                TurnoExtra castTurnoExtra = turnosService.casteoDeRequestATurnoExtra(turnoExtraRequest);
                turnoExtraService.saveTurnoExtra(jornadaId, turnoExtraId, castTurnoExtra);
                String mensajeResponse = "Los datos del turno extra se guardaron con exito!";
                if (cantidadDeHorasQueQuedarian < 30) {
                    mensajeResponse += " Aun necesita cargar mas hs para llegar a las 30hs minimas de esa semana";
                }
                return ResponseEntity
                        .ok()
                        .body(new MessageResponse(mensajeResponse));
            } catch (Exception e) {
                logger.error("Error: No se pudo guardar los datos del turno extra! {}", e);
                return ResponseEntity
                        .status(HttpStatus.EXPECTATION_FAILED)
                        .body(new MessageResponse("Error: Ups ocurrio algo al intentar guardar los datos del turno extra!"));
            }
        } else {
            return  controlarRequisitosDelTurno;
        }
    }

    @GetMapping("/get/extra/all/{id}")
    public ResponseEntity<?> getAllTurnosExtras(@PathVariable("id") Long idJornada){
        try {
            List<TurnoExtra> turnosExtras = turnoExtraService.getAllTurnosExtras(idJornada);
            return new ResponseEntity<>(turnosExtras, HttpStatus.OK);
        } catch(Exception e) {
            logger.error("Error: No se pudo obtener los turnos extras del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar obtener los turnos extras del empleado!"));
        }
    }

    @GetMapping("/get/extra/{id}")
    public ResponseEntity<?> getTurnoExtra(@PathVariable("id") Long idTurnoExtra){
        try {
            TurnoExtra turnoExtra = turnoExtraService.getTurnoById(idTurnoExtra).get();
            return new ResponseEntity<>(turnoExtra, HttpStatus.OK);
        } catch(Exception e) {
            logger.error("Error: No se pudo obtener el turno extra del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar obtener el turno extra del empleado!"));
        }
    }

    @DeleteMapping("/delete/extra/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteTurnoExtra(@PathVariable("id") Long idTurnoExtra){
        try {
            if(turnoExtraService.deleteTurnoExtra(idTurnoExtra))
                return ResponseEntity
                        .ok()
                        .body(new MessageResponse("El turno se elimino correctamente!"));
            else
                return ResponseEntity
                        .status(HttpStatus.NOT_MODIFIED)
                        .body(new MessageResponse("Error: Ups ocurrio algo al intentar borrar el turno extra!"));
        } catch (Exception e) {
            logger.error("Error: No se pudo borrar el turno normal! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Error: Ups ucurrio algo al intentar borrar el turno extra!"));
        }
    }
}
