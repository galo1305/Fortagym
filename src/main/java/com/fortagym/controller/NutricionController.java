package com.fortagym.controller;

import com.fortagym.model.Nutricion;
import com.fortagym.model.Rol;
import com.fortagym.model.Usuario;
import com.fortagym.repository.NutricionRepository;
import com.fortagym.repository.UsuarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/nutricion")
public class NutricionController {

    private static final Logger logger = LoggerFactory.getLogger(NutricionController.class);

    @Autowired
    private NutricionRepository nutricionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Listar usuarios para asignar cartilla
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioRepository.findByRol(Rol.USUARIO);
        model.addAttribute("usuarios", usuarios);
        return "nutricion/lista_usuarios";
    }

    // Formulario nueva cartilla
    @GetMapping("/nuevo/{idUsuario}")
    public String nuevaNutricion(@PathVariable Long idUsuario, Model model) {

    Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

    // Si ya existe una cartilla → redirigir a la vista
    if (nutricionRepository.existsByUsuarioId(idUsuario)) {
        return "redirect:/nutricion/ver/" +
                nutricionRepository.findByUsuarioId(idUsuario).get().getId();
    }

    Nutricion nutricion = new Nutricion();
    nutricion.setUsuario(usuario);

    // 🔥 AGREGADO → lista de usuarios SOLO con rol "usuario"
    List<Usuario> usuarios = usuarioRepository.findByRol(Rol.USUARIO);


    model.addAttribute("nutricion", nutricion);
    model.addAttribute("usuarios", usuarios); // <-- ESTE TE FALTABA

    return "nutricion/formulario";
}


    // Guardar cartilla
    @PostMapping("/guardar")
    public String guardar(@RequestParam("usuario.id") Long idUsuario,
            @ModelAttribute Nutricion nutricion) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        nutricion.setUsuario(usuario);
        nutricion.setFechaRegistro(LocalDateTime.now());

        nutricionRepository.save(nutricion);

        return "redirect:/nutricion/usuarios";
    }

    // Listado de todas las cartillas
    @GetMapping("/lista")
    public String listarCartillas(Model model) {
        model.addAttribute("cartillas", nutricionRepository.findAll());
        return "nutricion/lista_cartillas";
    }

    // Ver cartilla existente
    @GetMapping("/ver/{idNutricion}")
    public String verNutricion(@PathVariable Long idNutricion, Model model) {

    Nutricion nutricion = nutricionRepository.findById(idNutricion)
            .orElseThrow(() -> new IllegalArgumentException("Nutrición no encontrada"));

    model.addAttribute("nutricion", nutricion);

    // Lista de usuarios SOLO con rol "usuario"
    List<Usuario> usuarios = usuarioRepository.findByRol(Rol.USUARIO);
    model.addAttribute("usuarios", usuarios);

    return "nutricion/formulario";
}

}
