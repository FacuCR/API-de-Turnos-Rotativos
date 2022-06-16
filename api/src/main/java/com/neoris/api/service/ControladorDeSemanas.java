package com.neoris.api.service;

import com.neoris.api.entity.DiaLibre;
import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.entity.TurnoNormal;
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
    // ZonedDateTime se utiliza cuando queremos trabajar con fechas y tiempo pero agrega el factor de las zonas horarias,
    // para esto utiliza un ZoneId el cual es un identificador para diferentes zonas, el siguiente código obtiene
    // el ZoneId de Buenos Aires:
    private final ZoneId zoneId = ZoneId.of("America/Buenos_Aires" );
    private final int maxHorasDiarias = 12;

    @Override
    // Obtengo el numero de la semana del año
    // por ej: 04/01/2022 es la Semana 1 del año
    // 04/04/2022 seria la Semana 14 del año
    public int semanaDelAnio(Date fecha) {
        ZonedDateTime fechaElegida = ZonedDateTime.of(convertToLocalDateTimeViaSqlTimestamp(fecha), zoneId);
        return fechaElegida.get (IsoFields.WEEK_OF_WEEK_BASED_YEAR );
    }

    @Override
    // Obtengo el año de una fecha
    // por ej: 04/01/2022 devolveria 2022
    // 04/04/2016 devolveria 2016
    public int anioDeUnaFecha(Date fecha) {
        ZonedDateTime fechaElegida = ZonedDateTime.of(convertToLocalDateTimeViaSqlTimestamp(fecha), zoneId);
        return fechaElegida.get (IsoFields.WEEK_BASED_YEAR );
    }

    @Override
    // Calculo la cantidad de horas totales en la semana del turno que se quiere agregar
    public int cantDehorasSemana(List<Turno> turnos, Turno turnoNuevo) {
        try(Stream<Turno> turnosDeLaSemanaStream = turnos.stream()) {
            // Obtengo todas las horas que sean de la semana del turno nuevo
            int horasDeEsaSemana = turnosDeLaSemanaStream
                    // Obtengo los turnos que esten en la semana del nuevo turno
                    .filter(turno -> semanaDelAnio(turno.getFecha()) == semanaDelAnio(turnoNuevo.getFecha()))
                    // Lo mapeo por que solo me interesan las horas de esa semana
                    .map(Turno::getCantHoras)
                    // Mapeo el stream a int para poder usar la funcion sum
                    .mapToInt(Integer::intValue)
                    .sum();
            // Sumo todas las horas de esa semana y las retorno
            return horasDeEsaSemana;
        } catch(Exception e) {
            logger.error("Ocurrio un error al calcular las horas semanales: {}", e);
            return  -1;
        }
    }

    @Override
    // Calculo la cantidad de horas entre una fecha y otra
    public int cantDeHoras(Date fechaI, Date fechaF) {
        long segundos = (fechaF.getTime() - fechaI.getTime()) / 1000;
        return (int) segundos / 3600;
    }

    @Override
    // Convertir un Date a LocalDate
    public LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date fechaAConvertir) {
        return new java.sql.Timestamp(
                fechaAConvertir.getTime()).toLocalDateTime();
    }

    @Override
    // Comprobar si hay dos fechas en esa semana
    // uso este metodo para saber si hay dos dias libres en esa semana
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
            return !(semanasDelMismoAnio.size() < 2);
        } catch(Exception e) {
            logger.error("Ocurrio un error al comparar las fechas: {}", e);
            return  false;
        }
    }

    @Override
    // Metodo para saber si ya hay dos turnos en ese mismo dia
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
    // Comprobar si es el mismo usuario quieriendo guardar en el mismo turno
    public boolean isElMismoUsuarioEnElMismoTurno(List<Turno> turnos, Turno turnoNuevo) {
        try(Stream<Turno> turnoStream = turnos.stream()) {
            boolean result = turnoStream
                    .filter(turno -> turno.getFecha().compareTo(turnoNuevo.getFecha()) == 0)
                    .filter(turno -> turno.getTurno().equals(turnoNuevo.getTurno()))
                    .findAny().isPresent();
            return  result;
        } catch(Exception e) {
            logger.error("Ocurrio un error al revisar si el turno esta completamente ocupado: {}", e);
            return true;
        }
    }

    @Override // Chequear si se supera las horas de un dia de jornada
    public boolean isMaxDeHorasDeJornadaLaboralSuperada(List<Turno> turnos, Turno turnoNuevo) {
        try(Stream<Turno> turnoStream = turnos.stream()) {
            boolean result = turnoStream
                    .filter(turno -> turno.getFecha().compareTo(turnoNuevo.getFecha()) == 0) // Obtengo todos los turnos de ese dia
                    .map(Turno::getCantHoras) // Lo mapeo para que me de solo las horas de esos turnos
                    .mapToInt(Integer::intValue) // Convierto el stream en un strem int para operarlo
                    .sum() + turnoNuevo.getCantHoras() > maxHorasDiarias; // Si la suma de todas las horas de ese dia mas el nuevo turno que se quiere ingresar
                                                                          // supera el maximo de las horas diarias permitidas de una jornada devuelve true
            return  result;
        } catch(Exception e) {
            logger.error("Ocurrio un error al revisar si supera el maximo de horas diarias: {}", e);
            return true;
        }
    }

    @Override
    // Comprobar si ya hay un turno extra del usuario en esa fecha
    public boolean isTurnoExtraAsignadoEnEseDia(List<TurnoExtra> turnosExtras, Turno turnoExtra) {
        try(Stream<TurnoExtra> turnosExtrasStream = turnosExtras.stream()) {
            boolean result = turnosExtrasStream
                    .filter(turno -> turno.getFecha().compareTo(turnoExtra.getFecha()) == 0)
                    .findAny().isPresent();
            return result;
        } catch (Exception e) {
            logger.error("Ocurrio un error al revisar si ya tiene un turno extra asignado: {}", e);
            return false;
        }
    }

    @Override
    // Comprobar si ya hay un turno normal del usuario en esa fecha
    public boolean isTurnoNormalAsignadoEnEseDia(List<TurnoNormal> turnosNormales, Turno turnoNormal) {
        try(Stream<TurnoNormal> turnosNormalesStream = turnosNormales.stream()) {
            boolean result = turnosNormalesStream
                    .filter(turno -> turno.getFecha().compareTo(turnoNormal.getFecha()) == 0)
                    .count() > 0;
            return result;
        } catch (Exception e) {
            logger.error("Ocurrio un error al revisar si ya tiene un turno normal asignado: {}", e);
            return false;
        }
    }

    @Override
    // Comprobar si el usuario ya tiene una fecha libre asignada en ese dia
    public boolean isElMismoUsuarioConElMismoDiaLibre(List<DiaLibre> diasLibres, DiaLibre diaLibreNuevo) {
        try(Stream<DiaLibre> diaslibresStream = diasLibres.stream()) {
            boolean result = diaslibresStream
                    .filter(diaLibre -> diaLibre.getFecha().compareTo(diaLibreNuevo.getFecha()) == 0)
                    .findAny().isPresent();
            return  result;
        } catch(Exception e) {
            logger.error("Ocurrio un error al revisar si el dia libre esta ocupado: {}", e);
            return true;
        }
    }

    @Override
    // Comprobar si en esa semana el usuario ya tiene dos dias libres asignados
    public boolean isElMismoUsuarioConDosDiasLibresEnLaMismaSemana(List<DiaLibre> diasLibres, DiaLibre diaLibreNuevo) {
        try(Stream<DiaLibre> diasLibresStream = diasLibres.stream()) {
            List<Date> fechasDiasLibres = diasLibresStream.map(DiaLibre::getFecha).collect(Collectors.toList());
            return dosVecesEnLaMismaSemana(fechasDiasLibres, diaLibreNuevo.getFecha());
        } catch(Exception e) {
            logger.error("Ocurrio un error al revisar si ya tiene dos dias libres esa semana: {}", e);
            return  true;
        }
    }
}
