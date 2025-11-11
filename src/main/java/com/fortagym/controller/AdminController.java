package com.fortagym.controller;

import com.fortagym.model.Usuario;
import com.fortagym.model.Horario;
import com.fortagym.model.Rol;
import com.fortagym.repository.UsuarioRepository;
import com.fortagym.repository.HorarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioRepository usuarioRepository;

    public AdminController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Página principal del panel de admin
    @GetMapping
    public String verUsuarios(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("roles", Rol.values()); // para cambiar roles
        return "admin"; // Thymeleaf: templates/admin.html
    }

    // Cambiar rol
    @PostMapping("/cambiar-rol/{id}")
    public String cambiarRol(@PathVariable Long id, @RequestParam Rol rol) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null) {
            usuario.setRol(rol);
            usuarioRepository.save(usuario);
        }
        return "redirect:/admin";
    }

    // Eliminar usuario
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/admin";
    }



    @GetMapping("/horario/cambiar")
    public String mostrarFormularioCambiarHorario(Model model) {
        Horario horarioActual = horarioRepository.findTopByOrderByFechaSubidaDesc();
        model.addAttribute("horarioActual", horarioActual);
        return "horario_cambiar"; 
    }
@Autowired
private HorarioRepository horarioRepository;

@PostMapping("/horario/cambiar")
    public String cambiarHorario(@RequestParam("imagen") MultipartFile imagen,
                                 @RequestParam("nombre") String nombre) throws IOException {
        if (!imagen.isEmpty()) {
            Horario nuevoHorario = new Horario();
            nuevoHorario.setNombre(nombre);
            nuevoHorario.setDatos(imagen.getBytes());
            nuevoHorario.setFechaSubida(LocalDateTime.now());
            horarioRepository.save(nuevoHorario);
        }
        return "redirect:/admin/horario/cambiar";
    }

@GetMapping("/horario/imagen/{id}")
@ResponseBody
public byte[] verImagenHorario(@PathVariable Long id) {
    Horario horario = horarioRepository.findById(id).orElse(null);
    return (horario != null) ? horario.getDatos() : new byte[0];
}

}
