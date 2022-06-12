package com.neoris.api.service;

import com.neoris.api.entity.Empleado;
import com.neoris.api.entity.Usuario;
import com.neoris.api.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(EmpleadoService.class);

    @Override
    public List<Empleado> getAllEmpleados() {
        List<Empleado> empleados = null;

        try(Stream<Usuario> usuarioStream = usuarioRepository.findAll().stream()) {
            empleados = usuarioStream
                    .filter(user -> !Objects.isNull(user.getEmpleado()))
                    .map(user -> new Empleado(user.getEmpleado().getNombre(), user.getEmpleado().getApellido()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("No se pudo acceder a los empleados: {}", e);
        }

        return empleados;
    }

    @Override
    public boolean saveEmpleado(Empleado empleado, Long idUsuario) {
        boolean request = false;
        try {
            Optional<Usuario> user = usuarioRepository.findById(idUsuario);
            user.get().setEmpleado(empleado);
            usuarioRepository.save(user.get());
            request = true;
        } catch (Exception e) {
            logger.error("No se pudo guardar el empleado: {}", e);
        }
        return request;
    }

    @Override
    public Empleado getEmpleadoById(Long id) {
        return usuarioRepository.findById(id).get().getEmpleado();
    }
}
