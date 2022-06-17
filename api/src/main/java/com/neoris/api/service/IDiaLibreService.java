package com.neoris.api.service;

import com.neoris.api.entity.DiaLibre;
import com.neoris.api.exception.JornadaException;

import java.util.List;
import java.util.Optional;

public interface IDiaLibreService {
    List<DiaLibre> getAllDiasLibres(Long jornadaId);
    Optional<DiaLibre> getDiaLibreById(Long diaLibreId);
    void saveDiaLibre(Long jornadaId, DiaLibre diaLibre) throws JornadaException;
    void updateDiaLibre(Long jornadaId, Long diaLibreId, DiaLibre diaLibre) throws JornadaException;
    void deleteDiaLibre(Long diaLibreId) throws JornadaException;
}
