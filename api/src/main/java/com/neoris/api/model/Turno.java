package com.neoris.api.model;

import com.neoris.api.enums.ETurno;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Turno {
    private Date fecha;
    private ETurno turno;
    private int cantHoras;
}
