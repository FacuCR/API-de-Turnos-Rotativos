package com.neoris.api.repository;

import com.neoris.api.entity.DiaLibre;
import com.neoris.api.entity.JornadaLaboral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaLibreRepository extends JpaRepository<DiaLibre, Long> {
    List<DiaLibre> findAllByJornadaId(JornadaLaboral jornadaId);
}
