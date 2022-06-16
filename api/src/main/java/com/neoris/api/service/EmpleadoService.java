package com.neoris.api.service;

import com.neoris.api.entity.Empleado;
import com.neoris.api.entity.Usuario;
import com.neoris.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EmpleadoService implements IEmpleadoService{
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Empleado> getAllEmpleados() {
        Stream<Usuario> usuarioStream = usuarioRepository.findAll().stream();
        return usuarioStream
                .filter(user -> !Objects.isNull(user.getEmpleado())) // Cuando se crea el usuario sus nombres estan vacios y por eso los filtro
                .map(user -> new Empleado(user.getEmpleado().getNombre(), user.getEmpleado().getApellido())) // Lo mapeo a tipo Empleado
                .collect(Collectors.toList());
    }

    @Override
    public void saveEmpleado(Empleado empleado, Long idUsuario) {
        Optional<Usuario> user = usuarioRepository.findById(idUsuario);
        user.get().setEmpleado(empleado);
        usuarioRepository.save(user.get());
    }

    @Override
    public Empleado getEmpleadoById(Long id) {
        return usuarioRepository.findById(id).get().getEmpleado();
    }
}
