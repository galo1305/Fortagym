package com.fortagym.controller;

import com.fortagym.model.Nutricion;
import com.fortagym.model.Usuario;
import com.fortagym.repository.NutricionRepository;
import com.fortagym.repository.RutinaRepository;
import com.fortagym.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/nutricion")
public class NutricionController {

    @Autowired
    private NutricionRepository nutricionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RutinaRepository rutinaRepository; 

    // ✅ Listar todos los usuarios y verificar si tienen cartilla
    @GetMapping("/usuarios") 
    public String listarUsuarios(Model model) { 
        List<Usuario> usuarios = usuarioRepository.findAll(); 
        model.addAttribute("usuarios", usuarios); 
        return "nutricion/lista_usuarios"; }

    // ✅ Formulario nuevo
    @GetMapping("/nuevo/{idUsuario}")
    public String nuevaNutricion(@PathVariable("idUsuario") Long idUsuario, Model model) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            return "redirect:/nutricion/usuarios";
        }
        Nutricion nutricion = new Nutricion();
        nutricion.setUsuario(usuario);
        model.addAttribute("nutricion", nutricion);
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "nutricion/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Nutricion nutricion) {
    nutricion.setFechaRegistro(LocalDateTime.now());
    nutricionRepository.save(nutricion);

    // ✅ Redirige a la lista correcta
    return "redirect:/nutricion/usuarios";
}


    // ✅ Listar todas las cartillas de nutrición registradas
    @GetMapping("/lista")
    public String listarCartillas(Model model) {
        model.addAttribute("cartillas", nutricionRepository.findAll());
        return "nutricion/lista_cartillas";
    }
    // ✅ Ver nutrición existente (modo solo lectura o editable)
    @GetMapping("/ver/{idNutricion}")
    public String verNutricion(@PathVariable("idNutricion") Long idNutricion, Model model) {
    Nutricion nutricion = nutricionRepository.findById(idNutricion)
            .orElseThrow(() -> new IllegalArgumentException("Nutrición no encontrada"));

    // Lista de usuarios para el <select>
    List<Usuario> usuarios = usuarioRepository.findAll();

    model.addAttribute("nutricion", nutricion);
    model.addAttribute("usuarios", usuarios);

    return "nutricion/formulario";
}

}
