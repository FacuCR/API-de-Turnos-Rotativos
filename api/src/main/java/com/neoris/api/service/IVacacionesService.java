package com.neoris.api.service;

import com.neoris.api.entity.Vacaciones;
import com.neoris.api.exception.JornadaException;

import java.util.List;
import java.util.Optional;

public interface IVacacionesService {
    List<Vacaciones> getAllVacaciones(Long jornadaId);
    Optional<Vacaciones> getVacacionesById(Long vacacionesId);
    void saveVacaciones(Long jornadaId, Vacaciones vacaciones) throws JornadaException;
    void updateVacaciones(Long jornadaId, Long vacacionesId,Vacaciones vacacionesNuevas) throws JornadaException;
    void deleteVacaciones(Long vacacionesId) throws JornadaException;
}
