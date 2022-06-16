package com.neoris.api.service;

import com.neoris.api.entity.TurnoNormal;
import com.neoris.api.repository.JornadaLaboralRepository;
import com.neoris.api.repository.TurnoNormalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(TurnoNormalService.class);

    @Override
    public List<TurnoNormal> getAllTurnosNormales(Long jornadaId) {
        try {
            return turnoNormalRepository.findAllByJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
        } catch (Exception e) {
            logger.error("No se pudo acceder a los turnos normales: {}", e);
            return null;
        }
    }

    @Override
    public Optional<TurnoNormal> getTurnoById(Long turnoNormalId) {
        return turnoNormalRepository.findById(turnoNormalId);
    }

    @Override
    public void saveTurnoNormal(Long jornadaId, Long turnoNormalId, TurnoNormal turnoNormal) {
        turnoNormal.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
        turnoNormal.setIdTurnoNormal(turnoNormalId);
        turnoNormalRepository.save(turnoNormal);
    }

    @Override
    public void saveTurnoNormal(Long jornadaId, TurnoNormal turnoNormal) {
        turnoNormal.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
        turnoNormalRepository.save(turnoNormal);
    }

    @Override
    public void deleteTurnoNormal(Long idTurnoNormal) {
        if (turnoNormalRepository.existsById(idTurnoNormal)) {
            turnoNormalRepository.deleteById(idTurnoNormal);
        }
    }
}
