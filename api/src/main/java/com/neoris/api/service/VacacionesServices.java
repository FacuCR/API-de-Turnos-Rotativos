package com.neoris.api.service;

import com.neoris.api.entity.Vacaciones;
import com.neoris.api.exception.JornadaException;
import com.neoris.api.repository.VacacionesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VacacionesServices implements IVacacionesService{
    @Autowired
    private VacacionesRepository vacacionesRepository;
    @Autowired
    private IJornadaLaboralService jornadaLaboralService;

    @Override
    public List<Vacaciones> getAllVacaciones(Long jornadaId) {
        return vacacionesRepository.findAllByJornadaId(jornadaLaboralService.getJornadaLaboralById(jornadaId).get());
    }

    @Override
    public Optional<Vacaciones> getVacacionesById(Long vacacionesId) {
        return vacacionesRepository.findById(vacacionesId);
    }

    @Override
    public void saveVacaciones(Long jornadaId, Vacaciones vacaciones) throws JornadaException {
        if (jornadaLaboralService.getJornadaLaboralById(jornadaId).isPresent()) {
            vacaciones.setJornadaId(jornadaLaboralService.getJornadaLaboralById(jornadaId).get());
            vacacionesRepository.save(vacaciones);
        } else {
            throw new JornadaException("El id del usuario no se encontro por lo tanto no se pudo guardar las vacaciones");
        }
    }

    @Override
    public void updateVacaciones(Long jornadaId, Long vacacionesId, Vacaciones vacacionesNuevas) throws JornadaException {
        if (jornadaLaboralService.getJornadaLaboralById(jornadaId).isPresent()) {
            vacacionesNuevas.setJornadaId(jornadaLaboralService.getJornadaLaboralById(jornadaId).get());
            vacacionesNuevas.setIdVacaciones(vacacionesId);
            vacacionesRepository.save(vacacionesNuevas);
        } else {
            throw new JornadaException("El id del usuario no se encontro por lo tanto no se pudo guardar las vacaciones");
        }
    }

    @Override
    public void deleteVacaciones(Long vacacionesId) throws JornadaException {
        if (vacacionesRepository.existsById(vacacionesId)) {
            vacacionesRepository.deleteById(vacacionesId);
        } else {
            throw new JornadaException("El id de las vacaciones no se encontro por lo tanto no se pudo borrar las vacaciones");
        }
    }
}
