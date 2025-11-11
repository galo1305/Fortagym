package com.fortagym.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fortagym.model.Nutricion;
import com.fortagym.model.Usuario;

@Repository
public interface NutricionRepository extends JpaRepository<Nutricion, Long> {

    @Query("SELECT n FROM Nutricion n WHERE n.usuario = :usuario")
    Optional<Nutricion> findByUsuario(@Param("usuario") Usuario usuario);
    boolean existsByUsuarioId(Long usuarioId);
}
