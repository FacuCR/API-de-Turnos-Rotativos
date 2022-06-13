package com.neoris.api.service;

import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.model.Turno;
import com.neoris.api.payload.request.TurnoExtraRequest;
import com.neoris.api.payload.request.TurnoNormalRequest;
import com.neoris.api.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class TurnosService implements ITurnosService{
    @Autowired
    private ControladorDeSemanas controladorDeSemanas;

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
    public ResponseEntity<MessageResponse> controlarRequsitosDelTurno(List<Turno> turnosActuales, List<Turno> turnosActualesDeLosDemasUsuarios, Turno turnoNuevo, int cantMaxHsDeJornadaSemanal) {
        // Controlo que la fecha no sea de antes de la fecha actual
        Date fechaActual = new Date();
        if (turnoNuevo.getFecha().before(fechaActual)){
            SimpleDateFormat df = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No puedes viajar en el tiempo bro, la fecha de hoy es " + df.format(fechaActual) + " ingresa una fecha valida!"));
        }

        // Controlo que no se guarda en la misma jornada laboral el mismo turno
        if (!controladorDeSemanas.isElMismoUsuarioEnElMismoTurno(turnosActuales, turnoNuevo)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar los datos del turno normal por que ya tienes un " + turnoNuevo.getTurno() + " asignado ese dia!"));
        }

        int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(turnosActuales, turnoNuevo) + turnoNuevo.getCantHoras();
        // Controlo que la suma de la jornada laboral de esa semana mas el nuevo turno no supere las 48hs
        if (!(cantidadDeHorasQueQuedarian <= cantMaxHsDeJornadaSemanal)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar los datos del turno normal por que supera el limite de horas(48hs semanales)!"));
        }

        // Controlo que no se guarde si ya hay dos turnos ocupados en ese dia
        if (!controladorDeSemanas.isTurnoOcupado(turnosActualesDeLosDemasUsuarios, turnoNuevo)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar los datos del turno normal por que los " + turnoNuevo.getTurno() + " de ese dia estan ocupados!"));
        }

        return ResponseEntity
                .ok()
                .body(new MessageResponse(("")));
    }
}
