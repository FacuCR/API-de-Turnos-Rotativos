package com.neoris.api.service;

import com.neoris.api.entity.DiaLibre;
import com.neoris.api.exception.JornadaException;
import com.neoris.api.repository.DiaLibreRepository;
import com.neoris.api.repository.JornadaLaboralRepository;
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

    @Override
    public List<DiaLibre> getAllDiasLibres(Long jornadaId) {
        return diaLibreRepository.findAllByJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
    }

    @Override
    public Optional<DiaLibre> getDiaLibreById(Long diaLibreId) {
        return diaLibreRepository.findById(diaLibreId);
    }

    @Override
    public void saveDiaLibre(Long jornadaId, DiaLibre diaLibre) throws JornadaException {
        if (jornadaLaboralRepository.existsById(jornadaId)) {
            diaLibre.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
            diaLibreRepository.save(diaLibre);
        } else {
            throw new JornadaException("El id del usuario no se encontro por lo tanto no se pudo guardar el día libre");
        }

    }

    @Override
    public void updateDiaLibre(Long jornadaId, Long diaLibreId, DiaLibre diaLibre) throws JornadaException {
        if (jornadaLaboralRepository.existsById(jornadaId)) {
            diaLibre.setJornadaId(jornadaLaboralRepository.findById(jornadaId).get());
            diaLibre.setIdDiaLibre(diaLibreId);
            diaLibreRepository.save(diaLibre);
        } else {
            throw new JornadaException("El id del usuario no se encontro por lo tanto no se pudo guardar el día libre");
        }
    }

    @Override
    public void deleteDiaLibre(Long diaLibreId) throws JornadaException {
        if (diaLibreRepository.existsById(diaLibreId)) {
            diaLibreRepository.deleteById(diaLibreId);
        } else {
            throw new JornadaException("El id del día libre no se encontro por lo tanto no se pudo borrar el día libre que busca");
        }
    }
}
