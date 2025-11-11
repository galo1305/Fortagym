package com.fortagym.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fortagym.model.Promocion;
import com.fortagym.repository.PromocionRepository;

@Controller
public class EntrenadorController {

    @Autowired
    private PromocionRepository promocionRepository;

    @GetMapping("/entrenador_index")
    public String mostrarEntrenadorIndex(Model model) {
        Promocion promo = promocionRepository.findAll().stream().findFirst().orElse(null);

        model.addAttribute("promocion", promo);
        return "entrenador_index";
    }
}
