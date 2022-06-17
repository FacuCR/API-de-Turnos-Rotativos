package com.neoris.api.service;

import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.exception.JornadaException;
import com.neoris.api.repository.JornadaLaboralRepository;
import com.neoris.api.repository.TurnoNormalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurnoNormalService implements ITurnoNormalService{
    @Autowired
    private TurnoNormalRepository turnoNormalRepository;
    @Autowired
    private JornadaLaboralRepository jornadaLaboralRepository;

    @Override
    public List<TurnoNormal> getAllTurnosNormales(Long jornadaId) {
        return turnoNormalRepository.findAllByJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
    }

    @Override
    public Optional<TurnoNormal> getTurnoById(Long turnoNormalId) {
        return turnoNormalRepository.findById(turnoNormalId);
    }

    @Override
    public void saveTurnoNormal(Long jornadaId, Long turnoNormalId, TurnoNormal turnoNormal) throws JornadaException {
        if (jornadaLaboralRepository.existsById(jornadaId)) {
            turnoNormal.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
            turnoNormal.setIdTurnoNormal(turnoNormalId);
            turnoNormalRepository.save(turnoNormal);
        } else {
            throw new JornadaException("El id del usuario no se encontro por lo tanto no se pudo guardar el turno normal");
        }
    }

    @Override
    public void saveTurnoNormal(Long jornadaId, TurnoNormal turnoNormal) throws JornadaException {
        if (jornadaLaboralRepository.existsById(jornadaId)) {
            turnoNormal.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
            turnoNormalRepository.save(turnoNormal);
        } else {
            throw new JornadaException("El id del usuario no se encontro por lo tanto no se pudo guardar el turno normal");
        }
    }

    @Override
    public void deleteTurnoNormal(Long idTurnoNormal) throws JornadaException {
        if (turnoNormalRepository.existsById(idTurnoNormal)) {
            turnoNormalRepository.deleteById(idTurnoNormal);
        } else {
            throw new JornadaException("El id del turno no se encontro por lo tanto no se pudo borrar el turno normal");
        }
    }
}
