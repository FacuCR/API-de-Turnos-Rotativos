package com.neoris.api.payload.response;

import com.neoris.api.enums.ETurno;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class TurnoExtraResponse {
    private Long idTurnoNormal;
    private Date fecha;
    @Enumerated(EnumType.STRING)
    private ETurno turno;
    private int cantHoras;
    private Long usuarioId;
}
