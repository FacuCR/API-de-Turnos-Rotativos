package com.neoris.api.controller;

import com.neoris.api.entity.DiaLibre;
import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.entity.Vacaciones;
import com.neoris.api.exception.JornadaException;
import com.neoris.api.exception.MaxHsJornadaDiariaException;
import com.neoris.api.exception.MaxHsJornadaSemanalException;
import com.neoris.api.model.Turno;
import com.neoris.api.payload.request.*;
import com.neoris.api.payload.response.MessageResponse;
import com.neoris.api.payload.response.TurnosNormalesResponse;
import com.neoris.api.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
    private IJornadaLaboralService jornadaLaboralService;
    @Autowired
    private IDiaLibreService diaLibreService;
    @Autowired
    private IVacacionesService vacacionesService;
    private static final Logger logger = LoggerFactory.getLogger(JornadaLaboralController.class);
    private final int cantMinHsDeJornadaSemanal = 30;
    private final SimpleDateFormat df = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));

    // ========== TURNOS NORMALES ========== //

    @PostMapping("/save/normal/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> saveTurnoNormal(@Valid @RequestBody TurnoNormalRequest turnoNormalRequest, @PathVariable("id") Long jornadaId) {
        try {
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
            turnosService.controlarRequsitosDelTurno(turnosActualesDelUsuario, turnosActualesDeLosDemasUsuarios, turnoNuevo, jornadaId);
            turnosService.controlarRequisitosDelTurnoNormal(turnosNormalesActuales, turnoNuevo);

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
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar guardar los datos del turno normal!" + e.getMessage()));
        }


    }

    @PutMapping("/save/normal/{idJornada}/{idTurnoNormal}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateTurnoNormal(@Valid @RequestBody TurnoNormalRequest turnoNormalRequest, @PathVariable("idJornada") Long jornadaId, @PathVariable("idTurnoNormal") Long turnoNormalId) {
        try {
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
            turnosService.controlarRequisitosDelTurnoNormal(turnosNormalesActuales, turnoNuevo);
            turnosService.controlarRequsitosDelTurno(turnosActualesDelUsuario, turnosActualesDeLosDemasUsuarios, turnoNuevo, jornadaId);

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
        } catch (JornadaException e) {
            logger.error("Error: No se pudo guardar los datos del turno normal! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar actualizar los datos del turno normal!, Error: " + e.getMessage()));
        }
    }

    @GetMapping("/get/normal/all/{id}")
    public ResponseEntity<?> getAllTurnosNormales(@PathVariable("id") Long idJornada){
        try {
            Iterator<TurnoNormal> turnosNormales = turnoNormalService.getAllTurnosNormales(idJornada).iterator();
            List<TurnosNormalesResponse> turnosNormalesResponses = new ArrayList<>();
            TurnoNormal turno;
            TurnosNormalesResponse turnoResponse = new TurnosNormalesResponse();
            while(turnosNormales.hasNext()) {
                turno = turnosNormales.next();
                turnoResponse.setIdTurnoNormal(turno.getIdTurnoNormal());
                turnoResponse.setFecha(turno.getFecha());
                turnoResponse.setTurno(turno.getTurno());
                turnoResponse.setCantHoras(turno.getCantHoras());
                turnoResponse.setUsuarioId(idJornada);
                turnosNormalesResponses.add(turnoResponse);
                turnoResponse = new TurnosNormalesResponse();
            }
            return new ResponseEntity<>(turnosNormalesResponses, HttpStatus.OK);
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
        try {
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
            turnosService.controlarRequsitosDelTurno(turnosActualesDelUsuario, turnosActualesDeLosDemasUsuarios, turnoNuevo, jornadaId);
            turnosService.controlarRequisitosDelTurnoExtra(turnosExtrasActuales, turnoNuevo);

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
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar guardar los datos del turno extra!, Error: " + e.getMessage()));
        }


    }

    @PutMapping("/save/extra/{idJornada}/{idTurnoNormal}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateTurnoExtra(@Valid @RequestBody TurnoExtraRequest turnoExtraRequest, @PathVariable("idJornada") Long jornadaId, @PathVariable("idTurnoNormal") Long turnoExtraId) {
        try {
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

            turnosService.controlarRequisitosDelTurnoExtra(turnosExtrasActuales, turnoNuevo);
            turnosService.controlarRequsitosDelTurno(turnosActualesDelUsuario, turnosActualesDeLosDemasUsuarios, turnoNuevo, jornadaId);

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
        } catch (JornadaException e) {
            logger.error("Error: No se pudo guardar los datos del turno extra! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar actualizar los datos del turno extra!, Error: " + e.getMessage()));
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
        try {
            // Obtengo todos los turnos(Normales y extras) de la jornada del usuario casteados a Turno
            List<DiaLibre> diasLibresActualesDelUsuario = diaLibreService.getAllDiasLibres(jornadaId);

            // Casteo el request a DiaLibre
            DiaLibre diaLibreNuevo = new DiaLibre();
            diaLibreNuevo.setFecha(diaLibreRequest.getFecha());

            // Controlo las excepciones de los dias libres
            turnosService.controlarRequisitosDeDiaLibre(diasLibresActualesDelUsuario, diaLibreNuevo);

            // Obtengo todos los turnos(Normales y extras) por separado de la jornada del usuario
            List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);
            List<TurnoNormal> turnosNormalesActuales = turnoNormalService.getAllTurnosNormales(jornadaId);

            // Busco si hay un turno normal y/o extra ese dia para borrarlo
            String mensajeDeSeBorroAlgunTurno = turnosService.deleteAllTurnosDelDiaLibreElegido(diaLibreNuevo, turnosNormalesActuales, turnosExtrasActuales);

            // Guardo el DiaLibre
            diaLibreService.saveDiaLibre(jornadaId, diaLibreNuevo);

            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("El dia libre se asigno para la fecha: " + df.format(diaLibreNuevo.getFecha()) + " correctamente" + mensajeDeSeBorroAlgunTurno));
        } catch (JornadaException e) {
            logger.error("Error: No se pudo asignar el dia libre! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Ups ocurrio algo al intentar guardar el dia libre!, Error: " + e.getMessage()));
        }
    }

    @PutMapping("/save/libre/{jornadaId}/{diaLibreId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateDiaLibre(@Valid @RequestBody DiaLibreRequest diaLibreRequest, @PathVariable("jornadaId") Long jornadaId, @PathVariable("diaLibreId") Long diaLibreId) {
        try {
            // Obtengo todos los turnos(Normales y extras) de la jornada del usuario casteados a Turno
            List<DiaLibre> diasLibresActualesDelUsuario = diaLibreService.getAllDiasLibres(jornadaId);

            // Casteo el request a DiaLibre
            DiaLibre diaLibreNuevo = new DiaLibre();
            diaLibreNuevo.setFecha(diaLibreRequest.getFecha());

            // Controlo las excepciones de los dias libres
            turnosService.controlarRequisitosDeDiaLibre(diasLibresActualesDelUsuario, diaLibreNuevo);

            // Obtengo todos los turnos(Normales y extras) por separado de la jornada del usuario
            List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);
            List<TurnoNormal> turnosNormalesActuales = turnoNormalService.getAllTurnosNormales(jornadaId);

            // Busco si hay un turno normal y/o extra ese dia para borrarlo
            String mensajeDeSeBorroAlgunTurno = turnosService.deleteAllTurnosDelDiaLibreElegido(diaLibreNuevo, turnosNormalesActuales, turnosExtrasActuales);

            // Guardo el DiaLibre
            diaLibreService.updateDiaLibre(jornadaId,  diaLibreId, diaLibreNuevo);

            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("El dia libre se asigno para la fecha: " + df.format(diaLibreNuevo.getFecha()) + " correctamente" + mensajeDeSeBorroAlgunTurno));
        } catch (JornadaException e) {
            logger.error("Error: No se pudo asignar el dia libre! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Ups ocurrio algo al intentar actualizar el dia libre!, Error: " + e.getMessage()));
        }
    }

    @GetMapping("/get/libre/all/{id}")
    public ResponseEntity<?> getAllTurnosDiasLibres(@PathVariable("id") Long jornadaId){
        try {
            List<DiaLibre> diasLibres = diaLibreService.getAllDiasLibres(jornadaId);
            return new ResponseEntity<>(diasLibres, HttpStatus.OK);
        } catch(Exception e) {
            logger.error("Error: No se pudo obtener los días libres del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar obtener los días libres del empleado!"));
        }
    }

    @GetMapping("/get/libre/{id}")
    public ResponseEntity<?> getDiaLibre(@PathVariable("id") Long diaLibreId){
        try {
            DiaLibre diaLibre = diaLibreService.getDiaLibreById(diaLibreId).get();
            return new ResponseEntity<>(diaLibre, HttpStatus.OK);
        } catch(Exception e) {
            logger.error("Error: No se pudo obtener el dia libre del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar obtener el dia libre del empleado!"));
        }
    }

    @DeleteMapping("/delete/libre/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteDiaLibre(@PathVariable("id") Long diaLibreId){
        try {
            diaLibreService.deleteDiaLibre(diaLibreId);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("El dia libre se elimino correctamente!"));
        } catch (Exception e) {
            logger.error("Error: No se pudo borrar el día libre! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Error: Ups ucurrio algo al intentar borrar el dia libre!"));
        }
    }


    // ========== ANTIGUEDAD ========== //


    @GetMapping("/get/antiguedad/{id}")
    public ResponseEntity<MessageResponse> getAntiguedad(@PathVariable("id") Long jornadaLaboralId){
        try {
            int antiguedad = jornadaLaboralService.getAntiguedadByJornadaId(jornadaLaboralId);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("La antiguedad del usuario es de " + antiguedad + " años."));
        } catch(Exception e) {
            logger.error("Error: No se pudo obtener el turno extra del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar obtener la antiguedad del empleado!"));
        }
    }

    @PostMapping("/save/antiguedad/{id}/{antiguedad}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> saveAntiguedad(@PathVariable("id") Long jornadaId, @PathVariable("antiguedad") int antiguedad) {
        try {
            jornadaLaboralService.saveAntiguedad(jornadaId, antiguedad);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("La antiguedad del usuario se establecio en " + antiguedad + " años."));
        } catch(Exception e) {
            logger.error("Error: No se pudo obtener el turno extra del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar obtener la antiguedad del empleado!"));
        }
    }



    // ========== VACACIONES ========== //


    @PostMapping("/save/vacaciones/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> saveVacaciones(@PathVariable("id") Long jornadaId, @Valid @RequestBody VacacionesRequest vacacionesRequest) {
        try {
            Vacaciones nuevasVacaciones = new Vacaciones();
            nuevasVacaciones.setFechaInicio(vacacionesRequest.getFecha(), jornadaLaboralService.getAntiguedadByJornadaId(jornadaId));

            List<Vacaciones> todasLasVacacionesActuales = vacacionesService.getAllVacaciones(jornadaId);

            // Controlo las excepciones de las vacaciones
            turnosService.controlarRequisitosDeVacaciones(todasLasVacacionesActuales, nuevasVacaciones);

            // Obtengo todos los turnos(Normales y extras) por separado de la jornada del usuario
            List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);
            List<TurnoNormal> turnosNormalesActuales = turnoNormalService.getAllTurnosNormales(jornadaId);

            // Busco si hay un turnos normales y/o extras ese dia para borrarlos
            String mensajeDeSeBorroAlgunTurno = turnosService.deleteAllTurnosOcupadosPorLaVacacionElegida(nuevasVacaciones, turnosNormalesActuales, turnosExtrasActuales);

            vacacionesService.saveVacaciones(jornadaId, nuevasVacaciones);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("Las vacaciones se asignaron con exito inicio: " + df.format(nuevasVacaciones.getFechaInicio()) + " y final: " + df.format(nuevasVacaciones.getFechaFinal()) + mensajeDeSeBorroAlgunTurno));
        } catch (JornadaException e) {
            logger.error("Error: No se pudo guardar las vacaciones del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Ups ocurrio algo al intentar asignar las vacaciones del empleado!, Error: " + e.getMessage()));
        }
    }

    @PutMapping("/save/vacaciones/{jornadaId}/{vacacionesId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateVacaciones(@PathVariable("jornadaId") Long jornadaId, @PathVariable("vacacionesId") Long vacacionesId, @Valid @RequestBody VacacionesRequest vacacionesRequest) {
        try {
            Vacaciones nuevasVacaciones = new Vacaciones();
            nuevasVacaciones.setFechaInicio(vacacionesRequest.getFecha(), jornadaLaboralService.getAntiguedadByJornadaId(jornadaId));
            nuevasVacaciones.setIdVacaciones(vacacionesId);

            List<Vacaciones> todasLasVacacionesActuales = vacacionesService.getAllVacaciones(jornadaId);

            // Controlo las excepciones de las vacaciones
            turnosService.controlarRequisitosDeVacaciones(todasLasVacacionesActuales, nuevasVacaciones);

            // Obtengo todos los turnos(Normales y extras) por separado de la jornada del usuario
            List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);
            List<TurnoNormal> turnosNormalesActuales = turnoNormalService.getAllTurnosNormales(jornadaId);

            // Busco si hay un turnos normales y/o extras ese dia para borrarlos
            String mensajeDeSeBorroAlgunTurno = turnosService.deleteAllTurnosOcupadosPorLaVacacionElegida(nuevasVacaciones, turnosNormalesActuales, turnosExtrasActuales);

            vacacionesService.updateVacaciones(jornadaId, vacacionesId, nuevasVacaciones);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("Las vacaciones se asignaron con exito inicio: " + df.format(nuevasVacaciones.getFechaInicio()) + " y final: " + df.format(nuevasVacaciones.getFechaFinal()) + mensajeDeSeBorroAlgunTurno));
        } catch (JornadaException e) {
            logger.error("Error: No se pudo guardar las vacaciones del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Ups ocurrio algo al intentar asignar las vacaciones del empleado!, Error: " + e.getMessage()));
        }
    }

    @GetMapping("/get/vacaciones/{id}")
    public ResponseEntity<?> getVacaciones(@PathVariable("id") Long vacacionesId){
        try {
            Vacaciones vacaciones = vacacionesService.getVacacionesById(vacacionesId).get();
            return new ResponseEntity<>(vacaciones, HttpStatus.OK);
        } catch(Exception e) {
            logger.error("Error: No se pudo obtener las vacaciones del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar obtener las vacaciones del empleado!"));
        }
    }

    @GetMapping("/get/vacaciones/all/{id}")
    public ResponseEntity<?> getAllVacaciones(@PathVariable("id") Long jornadaId){
        try {
            List<Vacaciones> vacaciones = vacacionesService.getAllVacaciones(jornadaId);
            return new ResponseEntity<>(vacaciones, HttpStatus.OK);
        } catch(Exception e) {
            logger.error("Error: No se pudo obtener todas las vacaciones del empleado! {}", e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar obtener todas las vacaciones del empleado!"));
        }
    }


    @DeleteMapping("/delete/vacaciones/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteVacaciones(@PathVariable("id") Long vacacionesId){
        try {
            vacacionesService.deleteVacaciones(vacacionesId);
            return ResponseEntity
                    .ok()
                    .body(new MessageResponse("Las vacaciones se eliminaron correctamente!"));
        } catch (Exception e) {
            logger.error("Error: No se pudo borrar el turno normal! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar borrar las vacaciones!"));
        }
    }


    // ========== COMBINACION DE TURNOS ========== //


    @PostMapping("/save/combinacion/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Transactional
    // Decidi no implementar un controlador para los errores de combinacion ya que lo uso
    // solo una vez y lanzar los throws directamente
    public ResponseEntity<MessageResponse> saveCombinacionDeTurnos(@Valid @RequestBody CombinacionDeTurnosRequest combinacionDeTurnosRequest, @PathVariable("id") Long jornadaId) {
        try {
            if (combinacionDeTurnosRequest.getCantHorasNormal() + combinacionDeTurnosRequest.getCantHorasExtra() > 12) {
                throw new MaxHsJornadaDiariaException();
            }

            // Asigno la combinacion apropiadamente a cada tipo de turno
            TurnoNormalRequest turnoNormalRequest = new TurnoNormalRequest();
            turnoNormalRequest.setFecha(combinacionDeTurnosRequest.getFecha());
            turnoNormalRequest.setTurno(combinacionDeTurnosRequest.getTurnoNormal());
            turnoNormalRequest.setCantHoras(combinacionDeTurnosRequest.getCantHorasNormal());

            TurnoExtraRequest turnoExtraRequest = new TurnoExtraRequest();
            turnoExtraRequest.setFecha(combinacionDeTurnosRequest.getFecha());
            turnoExtraRequest.setTurno(combinacionDeTurnosRequest.getTurnoExtra());
            turnoExtraRequest.setCantHoras(combinacionDeTurnosRequest.getCantHorasExtra());

            // Debo castear los turnos normales y extras a turno ya que el service ControladorDeSemanas solo
            // trabaja con el tipo Turno

            // Obtengo todos los turnos normales del usuario
            List<TurnoNormal> turnosNormalesActuales = turnoNormalService.getAllTurnosNormales(jornadaId);

            // Obtengo todos los turnos(Normales y extras) de la jornada del usuario casteados al tipo Turno
            List<Turno> turnosActualesDelUsuario = turnosService.getAllTurnosDelUsuario(jornadaId);

            // Obtengo los turnos de los demas usuarios
            List<Turno> turnosActualesDeLosDemasUsuarios = turnosService.getAllTurnosDeLosDemasUsuarios(jornadaId);

            // Casteo el turno normal nuevo a Turno
            Turno turnoNormalNuevo = turnosService.casteoDeTurnoNormal(turnoNormalRequest);

            int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(turnosActualesDelUsuario, turnoNormalNuevo) + combinacionDeTurnosRequest.getCantHorasNormal() + combinacionDeTurnosRequest.getCantHorasExtra();
            if (cantidadDeHorasQueQuedarian > 48) {
                throw new MaxHsJornadaSemanalException();
            }

            // Controlo los requisitos para guardar el turno desde la clase TurnoService por que sino me quedaba mucho codigo duplicado
            turnosService.controlarRequsitosDelTurno(turnosActualesDelUsuario, turnosActualesDeLosDemasUsuarios, turnoNormalNuevo, jornadaId);
            turnosService.controlarRequisitosDelTurnoNormal(turnosNormalesActuales, turnoNormalNuevo);

            TurnoNormal castTurnoNormal = turnosService.casteoDeRequestATurnoNormal(turnoNormalRequest);
            turnoNormalService.saveTurnoNormal(jornadaId, castTurnoNormal);

            // Obtengo todos los turnos extras del usuario
            List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);

            // Casteo el turno normal nuevo a Turno
            Turno turnoExtraNuevo = turnosService.casteoDeTurnoExtra(turnoExtraRequest);

            // Controlo los requisitos para guardar el turno desde la clase TurnoService por que sino me quedaba mucho codigo duplicado
            turnosService.controlarRequsitosDelTurno(turnosActualesDelUsuario, turnosActualesDeLosDemasUsuarios, turnoExtraNuevo, jornadaId);
            turnosService.controlarRequisitosDelTurnoExtra(turnosExtrasActuales, turnoExtraNuevo);

            TurnoExtra castTurnoExtra = turnosService.casteoDeRequestATurnoExtra(turnoExtraRequest);
            turnoExtraService.saveTurnoExtra(jornadaId, castTurnoExtra);
            String mensajeResponse = "Los datos de los turnos se guardaron con exito!";
            if (cantidadDeHorasQueQuedarian < cantMinHsDeJornadaSemanal) {
                mensajeResponse += " Aun necesita cargar mas hs para llegar a las 30hs minimas de esa semana";
            }

            return ResponseEntity
                    .ok()
                    .body(new MessageResponse(mensajeResponse));
        } catch (JornadaException e) {
            logger.error("Error: No se pudo guardar los datos de los turnos! {}", e);
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse("Error: Ups ocurrio algo al intentar guardar los datos de los turnos! " + e.getMessage()));
        }


    }

}
