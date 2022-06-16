package com.neoris.api.service;

import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.exception.TurnoException;
import com.neoris.api.repository.JornadaLaboralRepository;
import com.neoris.api.repository.TurnoExtraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TurnoExtraService implements ITurnoExtraService{
    @Autowired
    private TurnoExtraRepository turnoExtraRepository;
    @Autowired
    private JornadaLaboralRepository jornadaLaboralRepository;

    @Override
    public List<TurnoExtra> getAllTurnosExtras(Long jornadaId) {
        return turnoExtraRepository.findAllByJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
    }

    @Override
    public Optional<TurnoExtra> getTurnoById(Long turnoExtraId) {
        return turnoExtraRepository.findById(turnoExtraId);
    }

    @Override
    public void saveTurnoExtra(Long jornadaId, Long turnoExtraId, TurnoExtra turnoExtra) throws TurnoException {
        if (jornadaLaboralRepository.existsById(jornadaId)) {
            turnoExtra.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
            turnoExtra.setIdTurnoExtra(turnoExtraId);
            turnoExtraRepository.save(turnoExtra);
        } else {
            throw new TurnoException("El id del usuario no se encontro por lo tanto no se pudo guardar el turno extra");
        }
    }

    @Override
    public void saveTurnoExtra(Long jornadaId, TurnoExtra turnoExtra) throws TurnoException {
        if (jornadaLaboralRepository.existsById(jornadaId)) {
            turnoExtra.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
            turnoExtraRepository.save(turnoExtra);
        } else {
            throw new TurnoException("El id del usuario no se encontro por lo tanto no se pudo guardar el turno extra");
        }
    }

    @Override
    public void deleteTurnoExtra(Long idTurnoExtra) throws TurnoException {
        if (turnoExtraRepository.existsById(idTurnoExtra)) {
            turnoExtraRepository.deleteById(idTurnoExtra);
        } else {
            throw new TurnoException("El id del usuario no se encontro por lo tanto no se pudo borrar el turno extra");
        }
    }
}
