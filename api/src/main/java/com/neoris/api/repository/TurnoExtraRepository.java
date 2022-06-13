package com.neoris.api.repository;

import com.neoris.api.entity.JornadaLaboral;
import com.neoris.api.entity.TurnoExtra;
import com.neoris.api.entity.TurnoNormal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurnoExtraRepository extends JpaRepository<TurnoExtra, Long> {
    List<TurnoNormal> findAllByJornadaId(JornadaLaboral jornadaId);
}
