package com.neoris.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.neoris.api.enums.ETurno;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class TurnoExtra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTurnoNormal;
    private Date fecha;
    @Enumerated(EnumType.STRING)
    private ETurno turno;
    private int cantHoras;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jornada_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private JornadaLaboral jornadaId;
}
