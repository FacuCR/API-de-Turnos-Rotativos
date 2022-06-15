package com.neoris.api.service;

import com.neoris.api.entity.DiaLibre;
import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.model.Turno;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface IControladorDeSemanas {
    int semanaDelAnio(Date fecha);
    boolean dosVecesEnLaMismaSemana(List<Date> fechas, Date fechaNueva);
    boolean isTurnoOcupado(List<Turno> turnos, Turno turnoNuevo);
    int anioDeUnaFecha(Date fecha);
    int cantDehorasSemana(List<Turno> turnos, Turno turnoNuevo);
    int cantDeHoras(Date fechaI, Date fechaF);
    LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date fechaAConvertir);
    boolean isElMismoUsuarioEnElMismoTurno(List<Turno> turnos, Turno turnoNuevo);
    boolean isMaxDeHorasDeJornadaLaboralSuperada(List<Turno> turnos, Turno turnoNuevo);
    boolean isTurnoExtraAsignadoEnEseDia(List<TurnoExtra> turnosExtras, Turno turnoExtra);
    boolean isTurnoNormalAsignadoEnEseDia(List<TurnoNormal> turnosNormales, Turno turnoNormal);
    boolean isElMismoUsuarioConElMismoDiaLibre(List<DiaLibre> diasLibres, DiaLibre diaLibreNuevo);
    boolean isElMismoUsuarioConDosDiasLibresEnLaMismaSemana(List<DiaLibre> diasLibres, DiaLibre diaLibreNuevo);
}
