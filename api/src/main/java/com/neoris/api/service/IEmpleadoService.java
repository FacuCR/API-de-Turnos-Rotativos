package com.neoris.api.service;

import com.neoris.api.entity.Empleado;

import java.util.List;

public interface IEmpleadoService {
    List<Empleado> getAllEmpleados();
    void saveEmpleado(Empleado empleado, Long idUsuario);
    Empleado getEmpleadoById(Long id);
}
