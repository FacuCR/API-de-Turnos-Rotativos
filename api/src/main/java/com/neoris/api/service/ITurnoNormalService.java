package com.neoris.api.service;

import com.neoris.api.entity.TurnoNormal;

import java.util.List;
import java.util.Optional;

public interface ITurnoNormalService {
    List<TurnoNormal> getAllTurnosNormales(Long jornadaId);
    Optional<TurnoNormal> getTurnoById(Long turnoNormalId);
    boolean saveTurnoNormal(Long jornadaId, Long turnoNormalId, TurnoNormal turnoNormal);
    boolean saveTurnoNormal(Long jornadaId, TurnoNormal turnoNormal);
}