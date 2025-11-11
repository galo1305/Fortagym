package com.fortagym.controller;

import com.fortagym.model.Usuario;
import com.fortagym.model.Nutricion;
import com.fortagym.model.Rutina;
import com.fortagym.model.DetalleRutina;
import com.fortagym.repository.UsuarioRepository;
import com.fortagym.repository.NutricionRepository;
import com.fortagym.repository.RutinaRepository;
import com.fortagym.repository.DetalleRutinaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cartilla")
public class CartillaController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NutricionRepository nutricionRepository;

    @Autowired
    private RutinaRepository rutinaRepository;

    @Autowired
    private DetalleRutinaRepository detalleRutinaRepository;

    // ✅ Vista de cartilla para el cliente
    @GetMapping("/{idUsuario}")
    public String verCartilla(@PathVariable Long idUsuario, Model model) {
        // Usuario
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            return "redirect:/";
        }

        // Nutrición
        Nutricion nutricion = nutricionRepository.findByUsuario(usuario).orElse(null);

        // Rutina
        Rutina rutina = rutinaRepository.findByUsuario(usuario).orElse(null);
        List<DetalleRutina> detalles = List.of();

        if (rutina != null) {
            detalles = detalleRutinaRepository.findByRutina(rutina);
        }

        // Pasamos datos al HTML
        model.addAttribute("usuario", usuario);
        model.addAttribute("nutricion", nutricion);
        model.addAttribute("rutina", rutina);
        model.addAttribute("detalles", detalles);

        return "cartilla/cartilla_digital"; // vista Thymeleaf
    }
}
