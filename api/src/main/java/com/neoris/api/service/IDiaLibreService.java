package com.neoris.api.service;

import com.neoris.api.entity.DiaLibre;

import java.util.List;
import java.util.Optional;

public interface IDiaLibreService {
    List<DiaLibre> getAllDiasLibres(Long jornadaId);
    Optional<DiaLibre> getDiaLibreById(Long diaLibreId);
    boolean saveDiaLibre(Long jornadaId, DiaLibre diaLibre);
    boolean updateDiaLibre(Long jornadaId, Long diaLibreId, DiaLibre diaLibre);
    boolean deleteDiaLibre(Long diaLibreId);
}
