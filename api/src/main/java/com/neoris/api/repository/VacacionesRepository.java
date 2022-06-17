package com.neoris.api.repository;

import com.neoris.api.entity.JornadaLaboral;
import com.neoris.api.entity.Vacaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacacionesRepository extends JpaRepository<Vacaciones,Long> {
    List<Vacaciones> findAllByJornadaId(JornadaLaboral jornadaId);
}
