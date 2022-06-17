package com.neoris.api.service;

import com.neoris.api.entity.JornadaLaboral;
import com.neoris.api.repository.JornadaLaboralRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JornadaLaboralService implements IJornadaLaboralService{
    @Autowired
    private JornadaLaboralRepository jornadaLaboralRepository;


    @Override
    public Optional<JornadaLaboral> getJornadaLaboralById(Long jornadaId) {
        return jornadaLaboralRepository.findById(jornadaId);
    }

    @Override
    public int getAntiguedadByJornadaId(Long jornadaId) {
        return jornadaLaboralRepository.findById(jornadaId).get().getAntiguedad();
    }

    @Override
    public void saveAntiguedad(Long jornadaId, int antiguedad) {
        JornadaLaboral jornadaLaboral = jornadaLaboralRepository.findById(jornadaId).get();
        jornadaLaboral.setAntiguedad(antiguedad);
        jornadaLaboralRepository.save(jornadaLaboral);
    }
}
