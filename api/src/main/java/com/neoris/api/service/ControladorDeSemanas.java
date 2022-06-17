package com.neoris.api.service;

import com.neoris.api.entity.DiaLibre;
import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.entity.Vacaciones;
import com.neoris.api.model.Turno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ControladorDeSemanas implements IControladorDeSemanas{
    // ZonedDateTime se utiliza cuando queremos trabajar con fechas y tiempo pero agrega el factor de las zonas horarias,
    // para esto utiliza un ZoneId el cual es un identificador para diferentes zonas, el siguiente código obtiene
    // el ZoneId de Buenos Aires:
    private final ZoneId zoneId = ZoneId.of("America/Buenos_Aires" );
    private final int maxHorasDiarias = 12;
    @Autowired
    private IDiaLibreService diaLibreService;

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
        Stream<Turno> turnosDeLaSemanaStream = turnos.stream();
        // Obtengo todas las horas que sean de la semana del turno nuevo
        int horasDeEsaSemana = turnosDeLaSemanaStream
                // Filtro los turnos del mismo año
                .filter(turno -> anioDeUnaFecha(turno.getFecha()) == anioDeUnaFecha(turnoNuevo.getFecha()))
                // Obtengo los turnos que esten en la semana del nuevo turno
                .filter(turno -> semanaDelAnio(turno.getFecha()) == semanaDelAnio(turnoNuevo.getFecha()))
                // Lo mapeo por que solo me interesan las horas de esa semana
                .map(Turno::getCantHoras)
                // Mapeo el stream a int para poder usar la funcion sum
                .mapToInt(Integer::intValue)
                .sum();
        // Sumo todas las horas de esa semana y las retorno
        return horasDeEsaSemana;
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
        Stream<Date> fechasStream = fechas.stream();
        List<Date> semanasDelMismoAnio = fechasStream
                // Obtengo todas las fechas del mismo año de la fecha nueva
                .filter(fecha -> anioDeUnaFecha(fecha) == anioDeUnaFecha(fechaNueva))
                // Obtengo todas las fechas que esten en la misma semana de la fecha nueva
                .filter(fecha -> semanaDelAnio(fecha) == semanaDelAnio(fechaNueva))
                .collect(Collectors.toList());

        // Si en la fecha nueva la lista de es semana tiene dos o mas (osea dos o mas dias libres en este caso)
        // el metodo retornara un false que luego usare para filtrar y no permitir al usuario
        // que agregue un dia libre mas en esa fecha
        return !(semanasDelMismoAnio.size() < 2);
    }

    @Override
    // Metodo para saber si ya hay dos turnos en ese mismo dia
    public boolean isTurnoOcupado(List<Turno> turnos, Turno turnoNuevo) {
        Stream<Turno> turnoStream = turnos.stream();
        int cantMaxDeTurnos = 2;
        int cantDeTurnos = (int) turnoStream
                .filter(turno -> turno.getFecha().compareTo(turnoNuevo.getFecha()) == 0)
                .filter(turno -> turno.getTurno().equals(turnoNuevo.getTurno()))
                .count();
        return cantDeTurnos < cantMaxDeTurnos;
    }

    @Override
    // Comprobar si es el mismo usuario quieriendo guardar en el mismo turno
    public boolean isElMismoUsuarioEnElMismoTurno(List<Turno> turnos, Turno turnoNuevo) {
        Stream<Turno> turnoStream = turnos.stream();
        boolean result = turnoStream
                .filter(turno -> turno.getFecha().compareTo(turnoNuevo.getFecha()) == 0)
                .filter(turno -> turno.getTurno().equals(turnoNuevo.getTurno()))
                .findAny().isPresent();
        return  result;
    }

    @Override // Chequear si se supera las horas de un dia de jornada
    public boolean isMaxDeHorasDeJornadaLaboralSuperada(List<Turno> turnos, Turno turnoNuevo) {
        Stream<Turno> turnoStream = turnos.stream();
        boolean result = turnoStream
                .filter(turno -> turno.getFecha().compareTo(turnoNuevo.getFecha()) == 0) // Obtengo todos los turnos de ese dia
                .map(Turno::getCantHoras) // Lo mapeo para que me de solo las horas de esos turnos
                .mapToInt(Integer::intValue) // Convierto el stream en un strem int para operarlo
                .sum() + turnoNuevo.getCantHoras() > maxHorasDiarias; // Si la suma de todas las horas de ese dia mas el nuevo turno que se quiere ingresar
        // supera el maximo de las horas diarias permitidas de una jornada devuelve true
        return  result;
    }

    @Override
    // Comprobar si ya hay un turno extra del usuario en esa fecha
    public boolean isTurnoExtraAsignadoEnEseDia(List<TurnoExtra> turnosExtras, Turno turnoExtra) {
        Stream<TurnoExtra> turnosExtrasStream = turnosExtras.stream();
        boolean result = turnosExtrasStream
                .filter(turno -> turno.getFecha().compareTo(turnoExtra.getFecha()) == 0)
                .findAny().isPresent();
        return result;
    }

    @Override
    // Comprobar si ya hay un turno normal del usuario en esa fecha
    public boolean isTurnoNormalAsignadoEnEseDia(List<TurnoNormal> turnosNormales, Turno turnoNormal) {
        Stream<TurnoNormal> turnosNormalesStream = turnosNormales.stream();
        boolean result = turnosNormalesStream
                .filter(turno -> turno.getFecha().compareTo(turnoNormal.getFecha()) == 0)
                .count() > 0;
        return result;
    }

    @Override
    // Comprobar si el usuario ya tiene una fecha libre asignada en ese dia
    public boolean isElMismoUsuarioConElMismoDiaLibre(List<DiaLibre> diasLibres, DiaLibre diaLibreNuevo) {
        Stream<DiaLibre> diaslibresStream = diasLibres.stream();
        return diaslibresStream
                .filter(diaLibre -> diaLibre.getFecha().compareTo(diaLibreNuevo.getFecha()) == 0)
                .findAny().isPresent();
    }

    @Override
    // Comprobar si en esa semana el usuario ya tiene dos dias libres asignados
    public boolean isElMismoUsuarioConDosDiasLibresEnLaMismaSemana(List<DiaLibre> diasLibres, DiaLibre diaLibreNuevo) {
        Stream<DiaLibre> diasLibresStream = diasLibres.stream();
        List<Date> fechasDiasLibres = diasLibresStream.map(DiaLibre::getFecha).collect(Collectors.toList());
        return dosVecesEnLaMismaSemana(fechasDiasLibres, diaLibreNuevo.getFecha());
    }

    @Override
    // Comprobar si hay un dia libre que coincide con la fecha, lo uso para mandarle la fecha del turno a agregar
    // y revisar si hay un dia libre en esa fecha
    public boolean isDiaLibre(Date fecha, Long jornadaId) {
        Stream<DiaLibre> todosLosDiasLibres = diaLibreService.getAllDiasLibres(jornadaId).stream();
        return todosLosDiasLibres.filter(dia -> dia.getFecha().compareTo(fecha) == 0).findAny().isPresent();
    }

    @Override
    public boolean isAlgunAnioCoincidente(List<Vacaciones> todasLasVacacionesActuales, Vacaciones nuevasVacaciones) {
        return todasLasVacacionesActuales.stream()
                .filter(vacacion -> anioDeUnaFecha(vacacion.getFechaInicio()) == anioDeUnaFecha(nuevasVacaciones.getFechaInicio()))
                .findAny().isPresent();
    }
}
