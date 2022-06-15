package com.neoris.api.service;

import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.repository.JornadaLaboralRepository;
import com.neoris.api.repository.TurnoExtraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(TurnoNormalService.class);

    @Override
    public List<TurnoExtra> getAllTurnosExtras(Long jornadaId) {
        try {
            return turnoExtraRepository.findAllByJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
        } catch (Exception e) {
            logger.error("No se pudo acceder a los turnos extras de este usuario: {}", e);
            return null;
        }
    }

    @Override
    public Optional<TurnoExtra> getTurnoById(Long turnoExtraId) {
        return turnoExtraRepository.findById(turnoExtraId);
    }

    @Override
    public boolean saveTurnoExtra(Long jornadaId, Long turnoExtraId, TurnoExtra turnoExtra) {
        try {
            turnoExtra.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
            turnoExtra.setIdTurnoExtra(turnoExtraId);
            turnoExtraRepository.save(turnoExtra);
            return true;
        } catch (Exception e) {
            logger.error("No se pudo guardar el turno extra: {}", e);
            return false;
        }
    }

    @Override
    public boolean saveTurnoExtra(Long jornadaId, TurnoExtra turnoExtra) {
        try {
            turnoExtra.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
            turnoExtraRepository.save(turnoExtra);
            return true;
        } catch (Exception e) {
            logger.error("No se pudo guardar el turno extra: {}", e);
            return false;
        }
    }

    @Override
    public boolean deleteTurnoExtra(Long idTurnoExtra) {
        if (turnoExtraRepository.existsById(idTurnoExtra)) {
            turnoExtraRepository.deleteById(idTurnoExtra);
            return true;
        }
        return false;
    }
}
