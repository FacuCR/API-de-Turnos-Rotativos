package com.neoris.api.service;

import com.neoris.api.entity.TurnoExtra;

import java.util.List;
import java.util.Optional;

public interface ITurnoExtraService {
    List<TurnoExtra> getAllTurnosExtras(Long jornadaId);
    Optional<TurnoExtra> getTurnoById(Long turnoExtraId);
    boolean saveTurnoExtra(Long jornadaId, Long turnoExtraId, TurnoExtra turnoExtra);
    boolean saveTurnoExtra(Long jornadaId, TurnoExtra turnoExtra);
    boolean deleteTurnoExtra(Long idTurnoExtra);
}
