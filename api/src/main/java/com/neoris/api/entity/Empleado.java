package com.neoris.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Empleado {
    @NotBlank
    @Size(max = 20)
    private String nombre;
    @NotBlank
    @Size(max = 20)
    private String apellido;
}
