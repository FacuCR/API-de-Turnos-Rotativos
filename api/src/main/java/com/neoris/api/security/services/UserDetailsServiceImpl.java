package com.neoris.api.security.services;

import com.neoris.api.entity.Usuario;
import com.neoris.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

// Uso UserDetailsService para obtener el objeto UserDetails.
// Obtenemos un objeto User personalizado completo usando UserRepository,
// luego construyo un objeto UserDetails usando el método static build().
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UsuarioRepository userRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username));
        return UserDetailsImpl.build(usuario);
    }
}
