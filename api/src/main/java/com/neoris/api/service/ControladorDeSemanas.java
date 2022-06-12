package com.neoris.api.service;

import com.neoris.api.model.Turno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ControladorDeSemanas implements IControladorDeSemanas{
    private static final Logger logger = LoggerFactory.getLogger(ControladorDeSemanas.class);
    private final ZoneId zoneId = ZoneId.of( "America/Buenos_Aires" );

    @Override
    public int semanaDelAnio(Date fecha) {
        ZonedDateTime fechaElegida = ZonedDateTime.of(convertToLocalDateTimeViaSqlTimestamp(fecha), zoneId);
        return fechaElegida.get (IsoFields.WEEK_OF_WEEK_BASED_YEAR );
    }

    @Override
    public int anioDeUnaFecha(Date fecha) {
        ZonedDateTime fechaElegida = ZonedDateTime.of(convertToLocalDateTimeViaSqlTimestamp(fecha), zoneId);
        return fechaElegida.get ( IsoFields.WEEK_BASED_YEAR );
    }

    @Override
    public int cantDehorasSemana(List<Turno> turnos, Turno turnoNuevo) {
        try(Stream<Turno> turnosDeLaSemanaStream = turnos.stream()) {
            // Obtengo todas las horas que sean de la semana del turno nuevo
            List<Integer> horasDeEsaSemana = turnosDeLaSemanaStream
                    .filter(turno -> semanaDelAnio(turno.getFecha()) == semanaDelAnio(turnoNuevo.getFecha()))
                    .map(Turno::getCantHoras)
                    .collect(Collectors.toList());
            // Sumo todas las horas de esa semana y las retorno
            return horasDeEsaSemana.stream().mapToInt(Integer::intValue).sum();
        } catch(Exception e) {
            logger.error("Ocurrio un error al calcular las horas semanales: {}", e);
            return  -1;
        }
    }

    @Override
    public int cantDeHoras(Date fechaI, Date fechaF) {
        long segundos = (fechaF.getTime() - fechaI.getTime()) / 1000;
        return (int) segundos / 3600;
    }

    @Override
    public LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date fechaAConvertir) {
        return new java.sql.Timestamp(
                fechaAConvertir.getTime()).toLocalDateTime();
    }

    @Override
    public boolean dosVecesEnLaMismaSemana(List<Date> fechas, Date fechaNueva) {
        // Obtengo todas las fechas del mismo año de la fecha nueva
        try(Stream<Date> fechasStream = fechas.stream()) {
            Stream<Date> fechasDelMismoAnio = fechasStream
                    .filter(fecha -> anioDeUnaFecha(fecha) == anioDeUnaFecha(fechaNueva));

            // Obtengo todas las fechas que esten en la misma semana de la fecha nueva
            List<Date> semanasDelMismoAnio = fechasDelMismoAnio
                    .filter(fecha -> semanaDelAnio(fecha) == semanaDelAnio(fechaNueva))
                    .collect(Collectors.toList());

            // Si en la fecha nueva la lista de es semana tiene dos o mas (osea dos o mas dias libres en este caso)
            // el metodo retornara un false que luego usare para filtrar y no permitir al usuario
            // que agregue un dia libre mas en esa fecha
            return semanasDelMismoAnio.size() < 2;
        } catch(Exception e) {
            logger.error("Ocurrio un error al comparar las fechas: {}", e);
            return  false;
        }
    }

    @Override
    public boolean isTurnoOcupado(List<Turno> turnos, Turno turnoNuevo) {
       try(Stream<Turno> turnoStream = turnos.stream()) {
           int cantMaxDeTurnos = 2;
           int cantDeTurnos = (int) turnoStream
                   .filter(turno -> turno.getFecha().compareTo(turnoNuevo.getFecha()) == 0)
                   .filter(turno -> turno.getTurno().equals(turnoNuevo.getTurno()))
                   .count();
           return cantDeTurnos < cantMaxDeTurnos;
       } catch(Exception e) {
           logger.error("Ocurrio un error al revisar si el turno esta completamente ocupado: {}", e);
           return  false;
       }
    }

    @Override
    public boolean isElMismoUsuarioEnElMismoTurno(List<Turno> turnos, Turno turnoNuevo) {
        try(Stream<Turno> turnoStream = turnos.stream()) {
            return turnoStream
                    .filter(turno -> turno.getTurno().equals(turnoNuevo.getTurno()))
                    .count() > 0;
        } catch(Exception e) {
            logger.error("Ocurrio un error al revisar si el turno esta completamente ocupado: {}", e);
            return true;
        }
    }
}
