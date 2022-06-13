package com.neoris.api.service;

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
}
