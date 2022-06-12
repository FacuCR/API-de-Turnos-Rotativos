package com.neoris.api.service;

import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.model.Turno;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface IControladorDeSemanas {
    int semanaDelAnio(Date fecha);
    boolean dosVecesEnLaMismaSemana(List<Date> fechas, Date fechaNueva);
    int anioDeUnaFecha(Date fecha);
    int cantDehorasSemana(List<Turno> turnos, Turno turnoNuevo);
    public int cantDeHoras(Date fechaI, Date fechaF);
    LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date fechaAConvertir);
}
