package com.neoris.api.controller;

import com.neoris.api.entity.DiaLibre;
import com.neoris.api.entity.JornadaLaboral;
import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.model.Turno;
import com.neoris.api.payload.request.DiaLibreRequest;
import com.neoris.api.payload.request.TurnoExtraRequest;
import com.neoris.api.payload.request.TurnoNormalRequest;
import com.neoris.api.payload.response.MessageResponse;
import com.neoris.api.repository.JornadaLaboralRepository;
import com.neoris.api.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Autowired
    private IDiaLibreService diaLibreService;
    private static final Logger logger = LoggerFactory.getLogger(JornadaLaboralController.class);
    private final int cantMinHsDeJornadaSemanal = 30;
    private final SimpleDateFormat df = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));

    // ========== TURNOS NORMALES ========== //

    @PostMapping("/save/normal/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> saveTurnoNormal(@Valid @RequestBody TurnoNormalRequest turnoNormalRequest, @PathVariable("id") Long jornadaId) {
        // Debo castear los turnos normales y extras a turno ya que el service ControladorDeSemanas solo
        // trabaja con el tipo Turno

        // Obtengo todos los turnos normales del usuario
        List<TurnoNormal> turnosNormalesActuales = turnoNormalService.getAllTurnosNormales(jornadaId);

        // Obtengo todos los turnos(Normales y extras) de la jornada del usuario casteados al tipo Turno
        List<Turno> turnosActualesDelUsuario = turnosService.getAllTurnosDelUsuario(jornadaId);

        // Obtengo los turnos de los demas usuarios
        List<Turno> turnosActualesDeLosDemasUsuarios = turnosService.getAllTurnosDeLosDemasUsuarios(jornadaId);

        // Casteo el turno normal nuevo a Turno
        Turno turnoNuevo = turnosService.casteoDeTurnoNormal(turnoNormalRequest);

        // Controlo los requisitos para guardar el turno desde la clase TurnoService por que sino me quedaba mucho codigo duplicado
        ResponseEntity<MessageResponse> controlarRequisitosDelTurno = turnosService.controlarRequsitosDelTurno(turnosActualesDelUsuario, turnosActualesDeLosDemasUsuarios, turnoNuevo, jornadaId);
        if (controlarRequisitosDelTurno.getStatusCode().equals(HttpStatus.OK)){
            controlarRequisitosDelTurno = turnosService.controlarRequisitosDelTurnoNormal(turnosNormalesActuales, turnoNuevo);
            if (controlarRequisitosDelTurno.getStatusCode().equals(HttpStatus.OK)) {
                try {
                    int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(turnosActualesDelUsuario, turnoNuevo) + turnoNuevo.getCantHoras();
                    TurnoNormal castTurnoNormal = turnosService.casteoDeRequestATurnoNormal(turnoNormalRequest);
                    turnoNormalService.saveTurnoNormal(jornadaId, castTurnoNormal);
                    String mensajeResponse = "Los datos del turno normal se guardaron con exito!";
                    if (cantidadDeHorasQueQuedarian < cantMinHsDeJornadaSemanal) {
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
        } else {
            return  controlarRequisitosDelTurno;
        }


    }

    @PutMapping("/save/normal/{idJornada}/{idTurnoNormal}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateTurnoNormal(@Valid @RequestBody TurnoNormalRequest turnoNormalRequest, @PathVariable("idJornada") Long jornadaId, @PathVariable("idTurnoNormal") Long turnoNormalId) {
        // Debo castear los turnos normales y extras a turno ya que el service ControladorDeSemanas solo
        // trabaja con el tipo Turno

        // Obtengo todos los turnos normales del usuario
        List<TurnoNormal> turnosNormalesActuales = turnoNormalService.getAllTurnosNormales(jornadaId);

        // Obtengo todos los turnos(Normales y extras) de la jornada del usuario casteados al tipo Turno
        List<Turno> turnosActualesDelUsuario = turnosService.getAllTurnosDelUsuario(jornadaId);

        // Obtengo los turnos de los demas usuarios
        List<Turno> turnosActualesDeLosDemasUsuarios = turnosService.getAllTurnosDeLosDemasUsuarios(jornadaId);

        // Casteo el turno normal nuevo a Turno
        Turno turnoNuevo = turnosService.casteoDeTurnoNormal(turnoNormalRequest);

        // Controlo los requisitos para guardar el turno desde la clase TurnoService por que sino me quedaba mucho codigo duplicado
        ResponseEntity<MessageResponse> controlarRequisitosDelTurno = turnosService.controlarRequsitosDelTurno(turnosActualesDelUsuario, turnosActualesDeLosDemasUsuarios, turnoNuevo, jornadaId);
        if (controlarRequisitosDelTurno.getStatusCode().equals(HttpStatus.OK)){
            controlarRequisitosDelTurno = turnosService.controlarRequisitosDelTurnoNormal(turnosNormalesActuales, turnoNuevo);
            if (controlarRequisitosDelTurno.getStatusCode().equals(HttpStatus.OK)) {
                try {
                    int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(turnosActualesDelUsuario, turnoNuevo) + turnoNuevo.getCantHoras();
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
            turnoNormalService.deleteTurnoNormal(idTurnoNormal);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("El turno se elimino correctamente!"));
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
        // Debo castear los turnos normales y extras a turno ya que el service ControladorDeSemanas solo
        // trabaja con el tipo Turno

        // Obtengo todos los turnos extras del usuario
        List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);

        // Obtengo todos los turnos(Normales y extras) de la jornada del usuario casteados al tipo Turno
        List<Turno> turnosActualesDelUsuario = turnosService.getAllTurnosDelUsuario(jornadaId);

        // Obtengo los turnos de los demas usuarios
        List<Turno> turnosActualesDeLosDemasUsuarios = turnosService.getAllTurnosDeLosDemasUsuarios(jornadaId);

        // Casteo el turno normal nuevo a Turno
        Turno turnoNuevo = turnosService.casteoDeTurnoExtra(turnoExtraRequest);

        // Controlo los requisitos para guardar el turno desde la clase TurnoService por que sino me quedaba mucho codigo duplicado
        ResponseEntity<MessageResponse> controlarRequisitosDelTurno = turnosService.controlarRequsitosDelTurno(turnosActualesDelUsuario, turnosActualesDeLosDemasUsuarios, turnoNuevo, jornadaId);
        if (controlarRequisitosDelTurno.getStatusCode().equals(HttpStatus.OK)){
            controlarRequisitosDelTurno = turnosService.controlarRequisitosDelTurnoExtra(turnosExtrasActuales, turnoNuevo);
            if (controlarRequisitosDelTurno.getStatusCode().equals(HttpStatus.OK)) {
                try {
                    int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(turnosActualesDelUsuario, turnoNuevo) + turnoNuevo.getCantHoras();
                    TurnoExtra castTurnoExtra = turnosService.casteoDeRequestATurnoExtra(turnoExtraRequest);
                    turnoExtraService.saveTurnoExtra(jornadaId, castTurnoExtra);
                    String mensajeResponse = "Los datos del turno extra se guardaron con exito!";
                    if (cantidadDeHorasQueQuedarian < cantMinHsDeJornadaSemanal) {
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
        } else {
            return  controlarRequisitosDelTurno;
        }


    }

    @PutMapping("/save/extra/{idJornada}/{idTurnoNormal}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateTurnoExtra(@Valid @RequestBody TurnoExtraRequest turnoExtraRequest, @PathVariable("idJornada") Long jornadaId, @PathVariable("idTurnoNormal") Long turnoExtraId) {
        // Debo castear los turnos normales y extras a turno ya que el service ControladorDeSemanas solo
        // trabaja con el tipo Turno

        // Obtengo todos los turnos extras del usuario
        List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);

        // Obtengo todos los turnos(Normales y extras) de la jornada del usuario casteados al tipo Turno
        List<Turno> turnosActualesDelUsuario = turnosService.getAllTurnosDelUsuario(jornadaId);

        // Obtengo los turnos de los demas usuarios
        List<Turno> turnosActualesDeLosDemasUsuarios = turnosService.getAllTurnosDeLosDemasUsuarios(jornadaId);

        // Casteo el turno normal nuevo a Turno
        Turno turnoNuevo = turnosService.casteoDeTurnoExtra(turnoExtraRequest);

        // Controlo los requisitos para guardar el turno desde la clase TurnoService por que sino me quedaba mucho codigo duplicado
        ResponseEntity<MessageResponse> controlarRequisitosDelTurno = turnosService.controlarRequsitosDelTurno(turnosActualesDelUsuario, turnosActualesDeLosDemasUsuarios, turnoNuevo, jornadaId);
        if (controlarRequisitosDelTurno.getStatusCode().equals(HttpStatus.OK)){
            controlarRequisitosDelTurno = turnosService.controlarRequisitosDelTurnoExtra(turnosExtrasActuales, turnoNuevo);
            if (controlarRequisitosDelTurno.getStatusCode().equals(HttpStatus.OK)) {
                try {
                    int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(turnosActualesDelUsuario, turnoNuevo) + turnoNuevo.getCantHoras();
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
            turnoExtraService.deleteTurnoExtra(idTurnoExtra);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("El turno se elimino correctamente!"));
        } catch (Exception e) {
            logger.error("Error: No se pudo borrar el turno normal! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Error: Ups ucurrio algo al intentar borrar el turno extra!"));
        }
    }


    // ========== DIAS LIBRES ========== //


    @PostMapping("/save/libre/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> saveDiaLibre(@Valid @RequestBody DiaLibreRequest diaLibreRequest, @PathVariable("id") Long jornadaId) {
        // Obtengo todos los turnos(Normales y extras) por separado de la jornada del usuario
        List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);
        List<TurnoNormal> turnosNormalesActuales = turnoNormalService.getAllTurnosNormales(jornadaId);

        // Obtengo todos los turnos(Normales y extras) de la jornada del usuario casteados a Turno
        List<DiaLibre> diasLibresActualesDelUsuario = diaLibreService.getAllDiasLibres(jornadaId);

        // Casteo el request a DiaLibre
        DiaLibre diaLibreNuevo = new DiaLibre();
        diaLibreNuevo.setFecha(diaLibreRequest.getFecha());

        ResponseEntity<MessageResponse> controlarRequisitosDeDiaLibre = turnosService.controlarRequisitosDeDiaLibre(diasLibresActualesDelUsuario, diaLibreNuevo);
        if (controlarRequisitosDeDiaLibre.getStatusCode().equals(HttpStatus.OK)) {
            try {
                // Busco si hay un turno normal y/o extra ese dia para borrarlo
                String mensajeDeSeBorroAlgunTurno = turnosService.deleteAllTurnosDelDiaLibreElegido(diaLibreNuevo, turnosNormalesActuales, turnosExtrasActuales);

                // Guardo el DiaLibre
                diaLibreService.saveDiaLibre(jornadaId, diaLibreNuevo);

                return ResponseEntity
                        .ok()
                        .body(new MessageResponse("El dia libre se asigno para la fecha: " + df.format(diaLibreNuevo.getFecha()) + " correctamente" + mensajeDeSeBorroAlgunTurno));
            } catch (Exception e) {
                logger.error("Error: No se pudo asignar el dia libre! {}", e);
                return ResponseEntity
                        .status(HttpStatus.EXPECTATION_FAILED)
                        .body(new MessageResponse("Error: Ups ucurrio algo al intentar asignar el dia libre!"));
            }
        } else {
            return  controlarRequisitosDeDiaLibre;
        }
    }

    @PutMapping("/save/libre/{jornadaId}/{diaLibreId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> saveDiaLibre(@Valid @RequestBody DiaLibreRequest diaLibreRequest, @PathVariable("jornadaId") Long jornadaId, @PathVariable("diaLibreId") Long diaLibreId) {
        // Obtengo todos los turnos(Normales y extras) por separado de la jornada del usuario
        List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);
        List<TurnoNormal> turnosNormalesActuales = turnoNormalService.getAllTurnosNormales(jornadaId);

        // Obtengo todos los turnos(Normales y extras) de la jornada del usuario casteados a Turno
        List<DiaLibre> diasLibresActualesDelUsuario = diaLibreService.getAllDiasLibres(jornadaId);

        // Casteo el request a DiaLibre
        DiaLibre diaLibreNuevo = new DiaLibre();
        diaLibreNuevo.setFecha(diaLibreRequest.getFecha());

        ResponseEntity<MessageResponse> controlarRequisitosDeDiaLibre = turnosService.controlarRequisitosDeDiaLibre(diasLibresActualesDelUsuario, diaLibreNuevo);
        if (controlarRequisitosDeDiaLibre.getStatusCode().equals(HttpStatus.OK)) {
            try {
                // Busco si hay un turno normal y/o extra ese dia para borrarlo
                String mensajeDeSeBorroAlgunTurno = turnosService.deleteAllTurnosDelDiaLibreElegido(diaLibreNuevo, turnosNormalesActuales, turnosExtrasActuales);

                // Guardo el DiaLibre
                diaLibreService.updateDiaLibre(jornadaId,  diaLibreId, diaLibreNuevo);

                return ResponseEntity
                        .ok()
                        .body(new MessageResponse("El dia libre se asigno para la fecha: " + df.format(diaLibreNuevo.getFecha()) + " correctamente" + mensajeDeSeBorroAlgunTurno));
            } catch (Exception e) {
                logger.error("Error: No se pudo asignar el dia libre! {}", e);
                return ResponseEntity
                        .status(HttpStatus.EXPECTATION_FAILED)
                        .body(new MessageResponse("Error: Ups ucurrio algo al intentar asignar el dia libre!"));
            }
        } else {
            return  controlarRequisitosDeDiaLibre;
        }
    }


}
