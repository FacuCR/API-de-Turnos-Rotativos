package com.neoris.api.service;

import com.neoris.api.entity.*;
import com.neoris.api.exception.*;
import com.neoris.api.model.Turno;
import com.neoris.api.payload.request.TurnoExtraRequest;
import com.neoris.api.payload.request.TurnoNormalRequest;
import com.neoris.api.payload.response.MessageResponse;
import com.neoris.api.repository.JornadaLaboralRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
// Habia pensado en agregarle metodos a la clase Turno, pero como necesitaba inyectar otros servicios
// opte por crear un service para todos los turnos en general
public class TurnosService implements ITurnosService{
    @Autowired
    private ControladorDeSemanas controladorDeSemanas;
    @Autowired
    private ITurnoNormalService turnoNormalService;
    @Autowired
    private ITurnoExtraService turnoExtraService;
    @Autowired
    private JornadaLaboralRepository jornadaLaboralRepository;
    private static final Logger logger = LoggerFactory.getLogger(TurnosService.class);
    private final int cantMaxHsDeJornadaSemanal = 48;
    private final SimpleDateFormat df = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-ES"));

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
    public List<Turno> casteoDeTurnosExtras(List<TurnoExtra> turnoExtraes) {
        List<Turno> castTurnosActuales = new ArrayList<>();
        for (TurnoExtra turnoNext : turnoExtraes) {
            Turno turno = new Turno();
            turno.setTurno(turnoNext.getTurno());
            turno.setFecha(turnoNext.getFecha());
            turno.setCantHoras(turnoNext.getCantHoras());
            castTurnosActuales.add(turno);
        }
        return castTurnosActuales;
    }

    @Override
    public Turno casteoDeTurnoExtra(TurnoExtraRequest turnoExtra) {
        Turno turnoCasteado = new Turno();
        turnoCasteado.setTurno(turnoExtra.getTurno());
        turnoCasteado.setFecha(turnoExtra.getFecha());
        turnoCasteado.setCantHoras(turnoExtra.getCantHoras());
        return turnoCasteado;
    }

    @Override
    public TurnoExtra casteoDeRequestATurnoExtra(TurnoExtraRequest turnoExtraRequest) {
        TurnoExtra castTurnoNormal = new TurnoExtra();
        castTurnoNormal.setTurno(turnoExtraRequest.getTurno());
        castTurnoNormal.setFecha(turnoExtraRequest.getFecha());
        castTurnoNormal.setCantHoras(turnoExtraRequest.getCantHoras());
        return castTurnoNormal;
    }

    @Override
    public void controlarRequisitosDelTurnoNormal(List<TurnoNormal> turnosNormales, Turno turnoNuevo) throws YaHayUnTurnoException {
        // Controlo que no tenga un turno normal asignado ya en ese dia
        if (!turnosNormales.isEmpty() && controladorDeSemanas.isTurnoNormalAsignadoEnEseDia(turnosNormales, turnoNuevo)){
            throw new YaHayUnTurnoException(turnoNuevo.getFecha(), "normal");
        }
    }

    @Override
    public void controlarRequisitosDelTurnoExtra(List<TurnoExtra> turnosExtras, Turno turnoNuevo) throws YaHayUnTurnoException {
        // Controlo que no tenga un turno extra asignado ya en ese dia
        if (!turnosExtras.isEmpty()  && controladorDeSemanas.isTurnoExtraAsignadoEnEseDia(turnosExtras, turnoNuevo)){
            throw new YaHayUnTurnoException(turnoNuevo.getFecha(), "extra");
        }

    }

    @Override
    // Elegi controlar los requisitos desde un metodo para no duplicar tanto codigo ya  que lo utilizaba varias veces
    public void controlarRequsitosDelTurno(List<Turno> turnosActuales, List<Turno> turnosActualesDeLosDemasUsuarios, Turno turnoNuevo, Long jornadaId) throws FechaAnteriorException, TurnoEnDiaLibreException, MismoTurnoException, MaxHsJornadaSemanalException, MaxHsJornadaDiariaException, MaxTurnosDiariosException, FechaDeVacacionesException {
        // Controlo que la fecha no sea de antes de la fecha actual
        // Date fechaActual = new Date();
        if (turnoNuevo.getFecha().before(new Date())){
            throw new FechaAnteriorException(turnoNuevo.getFecha());
        }

        // No se puede guardar el turno en un dia libre
        if (controladorDeSemanas.isDiaLibre(turnoNuevo.getFecha(), jornadaId)) {
            throw new TurnoEnDiaLibreException(turnoNuevo.getFecha());
        }

        // No se puede guardar un turno en fecha de vacaciones
        if (controladorDeSemanas.isAlgunaFechaDeVacaciones(turnoNuevo, jornadaId)) {
            throw new FechaDeVacacionesException();
        }

        // Controlo que no se guarda en la misma jornada laboral el mismo turno mas que nada para no asignar
        // un turno extra y un turno normal en el mismo momento
        // Osea, no dejar que sea posible por ejemplo guardar un turno normal a la noche y turno extra a la noche
        // se tiene que guardar el turno extra en otro momento
        if (controladorDeSemanas.isElMismoUsuarioEnElMismoTurno(turnosActuales, turnoNuevo)) {
            throw new MismoTurnoException(turnoNuevo.getTurno(), turnoNuevo.getFecha());
        }

        int cantidadDeHorasQueQuedarian = controladorDeSemanas.cantDehorasSemana(turnosActuales, turnoNuevo) + turnoNuevo.getCantHoras();
        // Controlo que la suma de la jornada laboral de esa semana mas el nuevo turno no supere las 48hs
        if (!(cantidadDeHorasQueQuedarian <= cantMaxHsDeJornadaSemanal)) {
            throw new MaxHsJornadaSemanalException();
        }

        // Controlo que la suma de todas las horas de ese dia mas el nuevo turno que se quiere ingresar no superen el limite permitido de 12
        if (controladorDeSemanas.isMaxDeHorasDeJornadaLaboralSuperada(turnosActuales, turnoNuevo)) {
            throw new MaxHsJornadaDiariaException();
        }

        // Controlo que no se guarde si ya hay dos turnos ocupados en ese dia
        if (!controladorDeSemanas.isTurnoOcupado(turnosActualesDeLosDemasUsuarios, turnoNuevo)) {
            throw new MaxTurnosDiariosException(turnoNuevo.getTurno());
        }

    }

    @Override
    // Obtengo todos los turnos de la jornada del usuario para castearlos a Turno
    public List<Turno> getAllTurnosDelUsuario(Long jornadaId) {
        List<TurnoNormal> turnosActuales = turnoNormalService.getAllTurnosNormales(jornadaId);
        List<TurnoExtra> turnosExtrasActuales = turnoExtraService.getAllTurnosExtras(jornadaId);
        List<Turno> castTurnosActuales = casteoDeTurnosNormales(turnosActuales);
        castTurnosActuales.addAll(casteoDeTurnosExtras(turnosExtrasActuales));
        return castTurnosActuales;
    }

    @Override
    // Obtengo los demas idJornada para obtener los turnos de los otros usuarios(sacando la jornada del usuario actual)
    // para posteriormente chequear que no hayan dos turnos ocupados por otros usuarios ese mismo dia
    public List<Turno> getAllTurnosDeLosDemasUsuarios(Long jornadaId) {
        List<Long> todosLosIdsDeLasDemasJornadas = jornadaLaboralRepository.findAll().stream()
                .map(JornadaLaboral::getIdJornada)
                .filter(ids -> !(ids.equals(jornadaId)))
                .collect(Collectors.toList());
        List<TurnoNormal> turnosNormalesDeLosDemasUsuarios = new ArrayList<>();
        List<TurnoExtra> turnosExtrasDeLosDemasUsuarios = new ArrayList<>();
        for (Long idDeOtraJornada : todosLosIdsDeLasDemasJornadas) {
            turnosNormalesDeLosDemasUsuarios.addAll(turnoNormalService.getAllTurnosNormales(idDeOtraJornada));
            turnosExtrasDeLosDemasUsuarios.addAll(turnoExtraService.getAllTurnosExtras(idDeOtraJornada));
        }
        List<Turno> castTurnosActualesDeLosDemasUsuarios = casteoDeTurnosNormales(turnosNormalesDeLosDemasUsuarios);
        castTurnosActualesDeLosDemasUsuarios.addAll(casteoDeTurnosExtras(turnosExtrasDeLosDemasUsuarios));
        return castTurnosActualesDeLosDemasUsuarios;
    }

    @Override
    public void controlarRequisitosDeDiaLibre(List<DiaLibre> diasLibres, DiaLibre diaLibreNuevo) throws FechaAnteriorException, DiaLibreAsignadoException, DemasiadosDiasLibresException {
        // Controlo que la fecha no sea de antes de la fecha actual
        Date fechaActual = new Date();
        if (diaLibreNuevo.getFecha().before(fechaActual)){
            throw new FechaAnteriorException(diaLibreNuevo.getFecha());
        }

        // Controlo que no tenga un dia libre asignado ya en ese dia
        if (!diasLibres.isEmpty() && controladorDeSemanas.isElMismoUsuarioConElMismoDiaLibre(diasLibres, diaLibreNuevo)){
            throw new DiaLibreAsignadoException(diaLibreNuevo.getFecha());
        }

        // Controlo que en la semana el usuario no tenga mas de 2 días libres.
        if (!diasLibres.isEmpty() && controladorDeSemanas.isElMismoUsuarioConDosDiasLibresEnLaMismaSemana(diasLibres, diaLibreNuevo)) {
            throw new DemasiadosDiasLibresException();
        }

    }

    @Override
    @Transactional // Como realizo varias operaciones sobre distintas tablas de la BD ya que en caso de error tengo
    // que hacer un rollback y no guardar la informacion en ninguna de las tablas afectadas y en caso de que sea
    // exitoso entonces debe hacer un commit de toda la transaccion guardando toda la informacion en todas las tablas
    // afectadas

    // Busco si hay un turno normal y/o extra ese dia para borrarlo
    public String deleteAllTurnosDelDiaLibreElegido(DiaLibre diaLibreNuevo, List<TurnoNormal> turnosNormalesActuales, List<TurnoExtra> turnosExtrasActuales) {
        String mensajeDeSeBorroAlgunTurno = "";
        try {
            // Borro el turno normal de ese dia
            Long idTurnoNormalDeEseDia = turnosNormalesActuales.stream()
                    .filter(turno -> turno.getFecha().compareTo(diaLibreNuevo.getFecha()) == 0)
                    .map(TurnoNormal::getIdTurnoNormal)
                    .findFirst().get();
            turnoNormalService.deleteTurnoNormal(idTurnoNormalDeEseDia);

            // Borro el turno extra de ese dia
            Long idTurnoExtraDeEseDia = turnosExtrasActuales.stream()
                    .filter(turno -> turno.getFecha().compareTo(diaLibreNuevo.getFecha()) == 0)
                    .map(TurnoExtra::getIdTurnoExtra)
                    .findFirst().get();
            turnoExtraService.deleteTurnoExtra(idTurnoExtraDeEseDia);
            mensajeDeSeBorroAlgunTurno = " y los turnos de ese dia se borraron";

        } catch(Exception e) {
            logger.error("Ocurrio un error al borrar los turnos del dia libre que se quiere asignar: {}", e);
        } finally {
            return mensajeDeSeBorroAlgunTurno;
        }
    }

    @Override
    @Transactional
    // Busco si hay turnos normales y/o extras entre la fecha de inicio y final de las vacaciones para borrarlo
    public String deleteAllTurnosOcupadosPorLaVacacionElegida(Vacaciones vacacionesNuevas, List<TurnoNormal> turnosNormalesActuales, List<TurnoExtra> turnosExtrasActuales) {
        String mensajeDeSeBorroAlgunTurno = "";
        try {
            // Borro los turnos normales entre esas fechas
            Stream<TurnoNormal> turnosNormalesActualesStream = turnosNormalesActuales.stream();
            List<Long> idsTurnosNormalesABorrar = turnosNormalesActualesStream
                    // Filtro todos los turnos que tengan la fecha entre la fecha de inicio y final de las vacaciones nuevas
                    .filter(turno ->
                            turno.getFecha().after(vacacionesNuevas.getFechaInicio())
                                    && turno.getFecha().before(vacacionesNuevas.getFechaFinal())
                                    || turno.getFecha().compareTo(vacacionesNuevas.getFechaInicio()) == 0 // y los que son iguales a la fecha de inicio
                                    || turno.getFecha().compareTo(vacacionesNuevas.getFechaFinal()) == 0 // o final tambien
                            )
                    .map(TurnoNormal::getIdTurnoNormal)
                    .collect(Collectors.toList());
            for (Long idTurnoNormal : idsTurnosNormalesABorrar) {
                turnoNormalService.deleteTurnoNormal(idTurnoNormal);
            }

            // Borro los turnos extras entre esas fechas
            Stream<TurnoExtra> turnosExtrasActualesStream = turnosExtrasActuales.stream();
            List<Long> idsTurnosExtrasDeEseDia = turnosExtrasActualesStream
                    // Las fechas que estan entre el inicio y el final de las vacaciones
                    .filter(turno ->
                            turno.getFecha().after(vacacionesNuevas.getFechaInicio())
                                    && turno.getFecha().before(vacacionesNuevas.getFechaFinal())
                                    || turno.getFecha().compareTo(vacacionesNuevas.getFechaInicio()) == 0 // y los que son iguales a la fecha de inicio
                                    || turno.getFecha().compareTo(vacacionesNuevas.getFechaFinal()) == 0 // o final tambien
                    )
                    .map(TurnoExtra::getIdTurnoExtra)
                    .collect(Collectors.toList());
            for (Long idTurnoExtra : idsTurnosExtrasDeEseDia) {
                turnoExtraService.deleteTurnoExtra(idTurnoExtra);
            }

            mensajeDeSeBorroAlgunTurno = " y los turnos de esas fechas se borraron";

        } catch(Exception e) {
            logger.error("Ocurrio un error al borrar los turnos entre las fechas de las vacaciones que se quiere asignar: {}", e);
        } finally {
            return mensajeDeSeBorroAlgunTurno;
        }
    }

    @Override
    public void controlarRequisitosDeVacaciones(List<Vacaciones> todasLasVacaciones, Vacaciones nuevasVacaciones) throws FechaAnteriorException, VacacionesException {
        // Controlo que la fecha no sea de antes de la fecha actual
        Date fechaActual = new Date();
        if (nuevasVacaciones.getFechaInicio().before(new Date())) {
            throw new FechaAnteriorException(nuevasVacaciones.getFechaInicio());
        }

        // Controlo que se guarde una vacacion por año
        if (!todasLasVacaciones.isEmpty() && controladorDeSemanas.isAlgunAnioCoincidente(todasLasVacaciones, nuevasVacaciones)) {
            throw new VacacionesException(controladorDeSemanas.anioDeUnaFecha(nuevasVacaciones.getFechaInicio()));
        }
    }

}
