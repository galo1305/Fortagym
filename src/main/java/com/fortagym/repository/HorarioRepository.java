package com.fortagym.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fortagym.model.Horario;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    Horario findTopByOrderByFechaSubidaDesc();
}
