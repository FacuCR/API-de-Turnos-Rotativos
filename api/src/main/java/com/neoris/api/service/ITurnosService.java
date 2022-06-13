package com.neoris.api.service;

import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.model.Turno;
import com.neoris.api.payload.request.TurnoExtraRequest;
import com.neoris.api.payload.request.TurnoNormalRequest;
import com.neoris.api.payload.response.MessageResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ITurnosService {
    List<Turno> casteoDeTurnosNormales(List<TurnoNormal> turnosNormales);
    Turno casteoDeTurnoNormal(TurnoNormalRequest turnoNormal);
    TurnoNormal casteoDeRequestATurnoNormal(TurnoNormalRequest turnoNormalRequest);
    List<Turno> casteoDeTurnosExtras(List<TurnoExtra> turnoExtraes);
    Turno casteoDeTurnoExtra(TurnoExtraRequest turnoExtra);
    TurnoExtra casteoDeRequestATurnoExtra(TurnoExtraRequest TurnoExtraRequest);
    ResponseEntity<MessageResponse> controlarRequsitosDelTurno(List<Turno> turnosActuales, List<Turno> turnosActualesDeLosDemasUsuarios, Turno turnoNuevo, int cantMaxHsDeJornadaSemanal);
}
