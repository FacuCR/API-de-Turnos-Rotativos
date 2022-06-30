package com.neoris.api.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class DiaLibreResponse {
    private Long idDiaLibre;
    private Date fecha;
    private Long usuarioId;
}
