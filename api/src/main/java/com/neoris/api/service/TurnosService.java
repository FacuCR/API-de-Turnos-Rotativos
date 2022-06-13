package com.neoris.api.service;

import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.model.Turno;
import com.neoris.api.payload.request.TurnoNormalRequest;
import com.neoris.api.payload.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    // Elegi controlar los requisitos desde un metodo para no duplicar tanto codigo ya  que lo utilizaba varias veces
    public ResponseEntity<MessageResponse> controlarRequsitosDelTurno(List<Turno> turnosActuales, Turno turnoNuevo, int cantMaxHsDeJornadaSemanal) {
        // Controlo que no se guarda en la misma jornada laboral el mismo turno
        if (!controladorDeSemanas.isElMismoUsuarioEnElMismoTurno(turnosActuales, turnoNuevo)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar los datos del turno normal por que ya tienes un " + turnoNuevo.getTurno() + " asignado ese dia!"));
        } else {
            int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(turnosActuales, turnoNuevo) + turnoNuevo.getCantHoras();
            // Controlo que la suma de la jornada laboral de esa semana mas el nuevo turno no supere las 48hs
            if (!(cantidadDeHorasQueQuedarian <= cantMaxHsDeJornadaSemanal)) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Error: No se pudo guardar los datos del turno normal por que supera el limite de horas(48hs semanales)!"));
            } else {
                // Controlo que no se guarde si ya hay dos turnos ocupados en ese dia
                if (!controladorDeSemanas.isTurnoOcupado(turnosActuales, turnoNuevo)) {
                    return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new MessageResponse("Error: No se pudo guardar los datos del turno normal por que los " + turnoNuevo.getTurno() + " de ese dia estan ocupados!"));
                }
            }
        }
        return ResponseEntity
                .ok()
                .body(new MessageResponse(("")));
    }
}
