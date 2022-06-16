package com.neoris.api.service;

import com.neoris.api.entity.DiaLibre;
import com.neoris.api.entity.JornadaLaboral;
import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.model.Turno;
import com.neoris.api.payload.request.TurnoExtraRequest;
import com.neoris.api.payload.request.TurnoNormalRequest;
import com.neoris.api.payload.response.MessageResponse;
import com.neoris.api.repository.JornadaLaboralRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
// Habia pensado en agregarle metodos a la clase Turno, pero como necesitaba inyectar otros servicios
// opte por crear un service para todos los turnos en general
public class TurnosService implements ITurnosService{
    @Autowired
    private ControladorDeSemanas controladorDeSemanas;
    @Autowired
    private ITurnoNormalService turnoNormalService;
    @Autowired
    private ITurnoExtraService turnoExtraService;
    @Autowired
    private JornadaLaboralRepository jornadaLaboralRepository;
    private static final Logger logger = LoggerFactory.getLogger(TurnosService.class);
    private final int cantMaxHsDeJornadaSemanal = 48;
    private final SimpleDateFormat df = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));

    @Override
    public List<Turno> casteoDeTurnosNormales(List<TurnoNormal> turnosNormales) {
        List<Turno> castTurnosActuales = new ArrayList<>();
        for (TurnoNormal turnoNext : turnosNormales) {
            Turno turno = new Turno();
            turno.setTurno(turnoNext.getTurno());
            turno.setFecha(turnoNext.getFecha());
            turno.setCantHoras(turnoNext.getCantHoras());
            castTurnosActuales.add(turno);
        }
        return castTurnosActuales;
    }

    @Override
    public Turno casteoDeTurnoNormal(TurnoNormalRequest turnoNormal) {
        Turno turnoCasteado = new Turno();
        turnoCasteado.setTurno(turnoNormal.getTurno());
        turnoCasteado.setFecha(turnoNormal.getFecha());
        turnoCasteado.setCantHoras(turnoNormal.getCantHoras());
        return turnoCasteado;
    }

    @Override
    public TurnoNormal casteoDeRequestATurnoNormal(TurnoNormalRequest turnoNormalRequest) {
        TurnoNormal castTurnoNormal = new TurnoNormal();
        castTurnoNormal.setTurno(turnoNormalRequest.getTurno());
        castTurnoNormal.setFecha(turnoNormalRequest.getFecha());
        castTurnoNormal.setCantHoras(turnoNormalRequest.getCantHoras());
        return castTurnoNormal;
    }

    @Override
    public List<Turno> casteoDeTurnosExtras(List<TurnoExtra> turnoExtraes) {
        List<Turno> castTurnosActuales = new ArrayList<>();
        for (TurnoExtra turnoNext : turnoExtraes) {
            Turno turno = new Turno();
            turno.setTurno(turnoNext.getTurno());
            turno.setFecha(turnoNext.getFecha());
            turno.setCantHoras(turnoNext.getCantHoras());
            castTurnosActuales.add(turno);
        }
        return castTurnosActuales;
    }

    @Override
    public Turno casteoDeTurnoExtra(TurnoExtraRequest turnoExtra) {
        Turno turnoCasteado = new Turno();
        turnoCasteado.setTurno(turnoExtra.getTurno());
        turnoCasteado.setFecha(turnoExtra.getFecha());
        turnoCasteado.setCantHoras(turnoExtra.getCantHoras());
        return turnoCasteado;
    }

    @Override
    public TurnoExtra casteoDeRequestATurnoExtra(TurnoExtraRequest turnoExtraRequest) {
        TurnoExtra castTurnoNormal = new TurnoExtra();
        castTurnoNormal.setTurno(turnoExtraRequest.getTurno());
        castTurnoNormal.setFecha(turnoExtraRequest.getFecha());
        castTurnoNormal.setCantHoras(turnoExtraRequest.getCantHoras());
        return castTurnoNormal;
    }

    @Override
    // Elegi controlar los requisitos desde un metodo para no duplicar tanto codigo ya  que lo utilizaba varias veces
    public ResponseEntity<MessageResponse> controlarRequsitosDelTurno(List<Turno> turnosActuales, List<Turno> turnosActualesDeLosDemasUsuarios, Turno turnoNuevo, List<TurnoExtra> turnosExtras, List<TurnoNormal> turnosNormales) {
        // Controlo que la fecha no sea de antes de la fecha actual
        Date fechaActual = new Date();
        if (turnoNuevo.getFecha().before(fechaActual)){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No puedes viajar en el tiempo bro, la fecha de hoy es " + df.format(fechaActual) + " ingresa una fecha valida!"));
        }

        // Controlo que no se guarda en la misma jornada laboral el mismo turno
        if (controladorDeSemanas.isElMismoUsuarioEnElMismoTurno(turnosActuales, turnoNuevo)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar los datos del turno por que ya tienes un " + turnoNuevo.getTurno() + " asignado ese dia!"));
        }

        // Controlo que no tenga un turno extra asignado ya en ese dia
        if (!turnosExtras.isEmpty() && controladorDeSemanas.isTurnoExtraAsignadoEnEseDia(turnosExtras, turnoNuevo)){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar el turno extra por que ya tienes un turno extra asignado ese dia!"));
        }

        // Controlo que no tenga un turno normal asignado ya en ese dia
        if (!turnosNormales.isEmpty() && controladorDeSemanas.isTurnoNormalAsignadoEnEseDia(turnosNormales, turnoNuevo)){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar el turno normal por que ya tienes un turno normal asignado ese dia!"));
        }

        int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(turnosActuales, turnoNuevo) + turnoNuevo.getCantHoras();
        // Controlo que la suma de la jornada laboral de esa semana mas el nuevo turno no supere las 48hs
        if (!(cantidadDeHorasQueQuedarian <= cantMaxHsDeJornadaSemanal)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar los datos del turno por que supera el limite de horas(48hs semanales)!"));
        }

        // Controlo que la suma de todas las horas de ese dia mas el nuevo turno que se quiere ingresar no superen el limite permitido de 12
        if (controladorDeSemanas.isMaxDeHorasDeJornadaLaboralSuperada(turnosActuales, turnoNuevo)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar los datos del turno por que supera el limite de horas diarias(12hs)!"));
        }

        // Controlo que no se guarde si ya hay dos turnos ocupados en ese dia
        if (!controladorDeSemanas.isTurnoOcupado(turnosActualesDeLosDemasUsuarios, turnoNuevo)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar los datos del turno por que los " + turnoNuevo.getTurno() + " de ese dia estan ocupados!"));
        }

        return ResponseEntity
                .ok()
                .body(new MessageResponse(("")));
    }

    @Override
    // Obtengo todos los turnos de la jornada del usuario para castearlos a Turno
    public List<Turno> getAllTurnosDelUsuario(Long jornadaId) {
        List<TurnoNormal> turnosActuales = turnoNormalService.getAllTurnosNormales(jornadaId);
        List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);
        List<Turno> castTurnosActuales = casteoDeTurnosNormales(turnosActuales);
        castTurnosActuales.addAll(casteoDeTurnosExtras(turnosExtrasActuales));
        return castTurnosActuales;
    }

    @Override
    // Obtengo los demas idJornada para obtener los turnos de los otros usuarios(sacando la jornada del usuario actual)
    // para posteriormente chequear que no hayan dos turnos ocupados por otros usuarios ese mismo dia
    public List<Turno> getAllTurnosDeLosDemasUsuarios(Long jornadaId) {
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
        List<Turno> castTurnosActualesDeLosDemasUsuarios = casteoDeTurnosNormales(turnosNormalesDeLosDemasUsuarios);
        castTurnosActualesDeLosDemasUsuarios.addAll(casteoDeTurnosExtras(turnosExtrasDeLosDemasUsuarios));
        return castTurnosActualesDeLosDemasUsuarios;
    }

    @Override
    public ResponseEntity<MessageResponse> controlarRequisitosDeDiaLibre(List<DiaLibre> diasLibres, DiaLibre diaLibreNuevo) {
        // Controlo que la fecha no sea de antes de la fecha actual
        Date fechaActual = new Date();
        if (diaLibreNuevo.getFecha().before(fechaActual)){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No puedes viajar en el tiempo bro, la fecha de hoy es " + df.format(fechaActual) + " ingresa una fecha valida!"));
        }

        // Controlo que no tenga un dia libre asignado ya en ese dia
        if (!diasLibres.isEmpty() && controladorDeSemanas.isElMismoUsuarioConElMismoDiaLibre(diasLibres, diaLibreNuevo)){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar el dia libre por que ya tienes un dia libre asignado en ese fecha!"));
        }

        // Controlo que en la semana el usuario no tenga mas de 2 días libres.
        if (!diasLibres.isEmpty() && controladorDeSemanas.isElMismoUsuarioConDosDiasLibresEnLaMismaSemana(diasLibres, diaLibreNuevo)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar el dia libre por que ya tienes dos dias libres asignado en ese semana!"));
        }

        return ResponseEntity
                .ok()
                .body(new MessageResponse(("")));
    }

    @Override
    // Busco si hay un turno normal y/o extra ese dia para borrarlo
    public String deleteAllTurnosDelDiaLibreElegido(DiaLibre diaLibreNuevo, List<TurnoNormal> turnosNormalesActuales, List<TurnoExtra> turnosExtrasActuales) {
        String mensajeDeSeBorroAlgunTurno = "";
        try {
            Stream<TurnoNormal> turnosNormalesActualesStream = turnosNormalesActuales.stream();
            boolean isTurnoEnEseDia = turnosNormalesActualesStream.filter(turno -> turno.getFecha().compareTo(diaLibreNuevo.getFecha()) == 0).findAny().isPresent();
            if (isTurnoEnEseDia) {
                Long idTurnoNormalDeEseDia = turnosNormalesActualesStream
                        .filter(turno -> turno.getFecha().compareTo(diaLibreNuevo.getFecha()) == 0)
                        .map(TurnoNormal::getIdTurnoNormal)
                        .findFirst().orElse(null);
                turnoNormalService.deleteTurnoNormal(idTurnoNormalDeEseDia);
                mensajeDeSeBorroAlgunTurno = " y los turnos de ese dia se borraron";
            }
            turnosNormalesActualesStream.close();

            Stream<TurnoExtra> turnosExtrasActualesStream = turnosExtrasActuales.stream();
            isTurnoEnEseDia = turnosExtrasActualesStream.filter(turno -> turno.getFecha().compareTo(diaLibreNuevo.getFecha()) == 0).findAny().isPresent();
            if (isTurnoEnEseDia) {
                Long idTurnoExtraDeEseDia = turnosExtrasActuales.stream()
                        .filter(turno -> turno.getFecha().compareTo(diaLibreNuevo.getFecha()) == 0)
                        .map(TurnoExtra::getIdTurnoExtra)
                        .findFirst().orElse(null);
                turnoExtraService.deleteTurnoExtra(idTurnoExtraDeEseDia);
                mensajeDeSeBorroAlgunTurno = " y los turnos de ese dia se borraron";
            }
            turnosExtrasActualesStream.close();

            return mensajeDeSeBorroAlgunTurno;
        } catch(Exception e) {
            logger.error("Ocurrio un error al borrar los turnos del dia libre que se quiere asignar: {}", e);
            return mensajeDeSeBorroAlgunTurno;
        }
    }
}
