package com.fortagym.controller;

import com.fortagym.model.Horario;
import com.fortagym.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HorarioController {

    @Autowired
    private HorarioRepository horarioRepository;

    @GetMapping("/horario")
    public String verHorarios(Model model, Authentication auth) {
        Horario horario = horarioRepository.findTopByOrderByFechaSubidaDesc();
        model.addAttribute("horario", horario);

        String rol = auth.getAuthorities().iterator().next().getAuthority();
        String destino = "/index";

        if (rol.equals("ENTRENADOR")) {
            destino = "/entrenador_index";
        } else if (rol.equals("ADMIN")) {
            destino = "/admin";
        }

        model.addAttribute("destinoIndex", destino);
        return "horario";
    }
}
