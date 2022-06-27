package com.neoris.api.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmpleadosResponse {
    Long id;
    String nombre;
    String apellido;
    String username;
}
