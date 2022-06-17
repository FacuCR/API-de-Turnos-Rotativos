package com.neoris.api.repository;

import com.neoris.api.entity.JornadaLaboral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JornadaLaboralRepository extends JpaRepository<JornadaLaboral, Long> {
}
