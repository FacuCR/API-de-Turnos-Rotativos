package com.neoris.api.service;

import com.neoris.api.entity.DiaLibre;
import com.neoris.api.repository.DiaLibreRepository;
import com.neoris.api.repository.JornadaLaboralRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiaLibreService implements IDiaLibreService{
    @Autowired
    private DiaLibreRepository diaLibreRepository;
    @Autowired
    private JornadaLaboralRepository jornadaLaboralRepository;
    private static final Logger logger = LoggerFactory.getLogger(DiaLibreService.class);

    @Override
    public List<DiaLibre> getAllDiasLibres(Long jornadaId) {
        try {
            return diaLibreRepository.findAllByJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
        } catch (Exception e) {
            logger.error("No se pudo acceder a los dias libres de este usuario: {}", e);
            return null;
        }
    }

    @Override
    public Optional<DiaLibre> getDiaLibreById(Long diaLibreId) {
        return diaLibreRepository.findById(diaLibreId);
    }

    @Override
    public boolean saveDiaLibre(Long jornadaId, DiaLibre diaLibre) {
        try {
            diaLibre.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
            diaLibreRepository.save(diaLibre);
            return true;
        } catch (Exception e) {
            logger.error("No se pudo guardar el dia libre: {}", e);
            return false;
        }
    }

    @Override
    public boolean updateDiaLibre(Long jornadaId, Long diaLibreId, DiaLibre diaLibre) {
        try {
            diaLibre.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
            diaLibre.setIdDiaLibre(diaLibreId);
            diaLibreRepository.save(diaLibre);
            return true;
        } catch (Exception e) {
            logger.error("No se pudo guardar el dia libre: {}", e);
            return false;
        }
    }

    @Override
    public boolean deleteDiaLibre(Long diaLibreId) {
        if (diaLibreRepository.existsById(diaLibreId)) {
            diaLibreRepository.deleteById(diaLibreId);
            return true;
        }
        return false;
    }
}
