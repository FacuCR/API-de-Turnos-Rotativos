package com.neoris.api.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class VacacionesResponse {
    private Long idVacaciones;
    private Date fecha;
    private Date fechaFinal;
    private Long usuarioId;
}
