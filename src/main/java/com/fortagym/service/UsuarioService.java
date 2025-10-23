package com.fortagym.service;

import com.fortagym.model.Usuario;
import com.fortagym.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // usa el bean definido en SecurityConfig

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()) != null) {
            throw new EmailAlreadyExistsException("El correo ya está en uso");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        try {
            return usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException ex) {
            throw new EmailAlreadyExistsException("El correo ya está en uso");
        }
    }

    public boolean validarLogin(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario != null) {
            return passwordEncoder.matches(password, usuario.getPassword());
        }
        return false;
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public void guardar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }
}
