package com.neoris.api.service;

import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.exception.TurnoException;

import java.util.List;
import java.util.Optional;

public interface ITurnoExtraService {
    List<TurnoExtra> getAllTurnosExtras(Long jornadaId);
    Optional<TurnoExtra> getTurnoById(Long turnoExtraId);
    void saveTurnoExtra(Long jornadaId, Long turnoExtraId, TurnoExtra turnoExtra) throws TurnoException;
    void saveTurnoExtra(Long jornadaId, TurnoExtra turnoExtra) throws TurnoException;
    void deleteTurnoExtra(Long idTurnoExtra) throws TurnoException;
}
