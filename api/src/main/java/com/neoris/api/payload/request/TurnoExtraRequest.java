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
public class TurnoExtraRequest {
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd/MM/yyyy", timezone = "America/Buenos_Aires")
    private Date fecha;
    @Enumerated(EnumType.STRING)
    private ETurno turno;
    @Min(2)
    @Max(6)
    private int cantHoras;
}
