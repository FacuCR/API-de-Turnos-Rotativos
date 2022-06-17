package com.neoris.api.service;

import com.neoris.api.entity.JornadaLaboral;

import java.util.Optional;

public interface IJornadaLaboralService {
    Optional<JornadaLaboral> getJornadaLaboralById(Long jornadaId);
    int getAntiguedadByJornadaId(Long jornadaId);
    void saveAntiguedad(Long jornadaId, int antiguedad);
}
