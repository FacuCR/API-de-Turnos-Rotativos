package com.neoris.api.service;

import com.neoris.api.entity.TurnoExtra;
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
    public void saveTurnoExtra(Long jornadaId, Long turnoExtraId, TurnoExtra turnoExtra) {
        turnoExtra.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
        turnoExtra.setIdTurnoExtra(turnoExtraId);
        turnoExtraRepository.save(turnoExtra);
    }

    @Override
    public void saveTurnoExtra(Long jornadaId, TurnoExtra turnoExtra) {
        turnoExtra.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
        turnoExtraRepository.save(turnoExtra);
    }

    @Override
    public void deleteTurnoExtra(Long idTurnoExtra) {
        if (turnoExtraRepository.existsById(idTurnoExtra)) {
            turnoExtraRepository.deleteById(idTurnoExtra);
        }
    }
}
