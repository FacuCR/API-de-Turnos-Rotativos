package com.neoris.api.service;

import com.neoris.api.entity.DiaLibre;
import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.entity.Vacaciones;
import com.neoris.api.exception.FechaAnteriorException;
import com.neoris.api.exception.VacacionesException;
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
    ResponseEntity<MessageResponse> controlarRequsitosDelTurno(List<Turno> turnosActuales, List<Turno> turnosNormalesActualesDeLosDemasUsuarios, Turno turnoNuevo, Long jornadaId);
    ResponseEntity<MessageResponse> controlarRequisitosDelTurnoNormal(List<TurnoNormal> turnosNormales, Turno turnoNuevo);
    ResponseEntity<MessageResponse> controlarRequisitosDelTurnoExtra(List<TurnoExtra> turnosExtras, Turno turnoNuevo);
    List<Turno> getAllTurnosDelUsuario(Long jornadaId);
    List<Turno> getAllTurnosDeLosDemasUsuarios(Long jornadaId);
    ResponseEntity<MessageResponse> controlarRequisitosDeDiaLibre(List<DiaLibre> diasLibres, DiaLibre diaLibreNuevo);
    String deleteAllTurnosDelDiaLibreElegido(DiaLibre diaLibreNuevo, List<TurnoNormal> turnosNormalesActuales, List<TurnoExtra> turnosExtrasActuales);
    String deleteAllTurnosOcupadosPorLaVacacionElegida(Vacaciones vacacionesNuevas, List<TurnoNormal> turnosNormalesActuales, List<TurnoExtra> turnosExtrasActuales);
    void controlarRequisitosDeVacaciones(List<Vacaciones> todasLasVacaciones, Vacaciones nuevasVacaciones) throws FechaAnteriorException, VacacionesException;
}
