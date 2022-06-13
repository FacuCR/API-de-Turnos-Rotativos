package com.neoris.api.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neoris.api.enums.ETurno;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

@Getter
@Setter
public class TurnoNormalRequest {
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd/MM/yyyy")
    private Date fecha;
    @Enumerated(EnumType.STRING)
    private ETurno turno;
    @Min(6)
    @Max(8)
    private int cantHoras;
    private boolean isTurnoExtra;
}
