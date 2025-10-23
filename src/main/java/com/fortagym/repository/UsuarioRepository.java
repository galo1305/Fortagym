package com.fortagym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fortagym.model.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
}
