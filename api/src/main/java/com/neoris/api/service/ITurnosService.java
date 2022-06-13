package com.neoris.api.service;

import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.model.Turno;
import com.neoris.api.payload.request.TurnoNormalRequest;
import com.neoris.api.payload.response.MessageResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ITurnosService {
    List<Turno> casteoDeTurnosNormales(List<TurnoNormal> turnosNormales);
    Turno casteoDeTurnoNormal(TurnoNormalRequest turnoNormal);
    TurnoNormal casteoDeRequestATurnoNormal(TurnoNormalRequest turnoNormalRequest);
    ResponseEntity<MessageResponse> controlarRequsitosDelTurno(List<Turno> turnosActuales, List<Turno> turnosActualesDeLosDemasUsuarios, Turno turnoNuevo, int cantMaxHsDeJornadaSemanal);
}
