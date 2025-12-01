package com.fortagym.controller;

import com.fortagym.model.Usuario;
import com.fortagym.model.Rol;
import com.fortagym.repository.UsuarioRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UsuarioRepository usuarioRepository;

    public AdminController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ============================
    //  PAGINA PRINCIPAL ADMIN
    // ============================
    @GetMapping
    public String verUsuarios(Model model, Authentication auth) {

    logger.info("Cargando listado de usuarios para panel de admin");

    Usuario usuarioLog = null;
if (auth != null) {
    usuarioLog = usuarioRepository.findByEmail(auth.getName());
}
model.addAttribute("usuarioLog", usuarioLog);

    model.addAttribute("usuarios", usuarioRepository.findAll());
    model.addAttribute("roles", Rol.values());

    return "admin";
}




    // ============================
    // CAMBIAR ROL DE USUARIO
    // ============================
    @PostMapping("/cambiar-rol/{id}")
    public String cambiarRol(@PathVariable Long id, @RequestParam Rol rol) {

        logger.info("Intentando cambiar rol del usuario ID={} a {}", id, rol);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Usuario ID={} no encontrado", id);
                    return new RuntimeException("Usuario no encontrado");
                });

        usuario.setRol(rol);
        usuarioRepository.save(usuario);

        logger.info("Rol cambiado correctamente para ID={}", id);
        return "redirect:/admin";
    }


    // ============================
    // ELIMINAR USUARIO
    // ============================
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {

        logger.warn("Solicitud de eliminación del usuario ID={}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElse(null);

        if (usuario == null) {
            logger.warn("Intento de eliminar usuario ID={} pero no existe", id);
            return "redirect:/admin";
        }

        try {
            usuarioRepository.delete(usuario);
            logger.info("Usuario ID={} eliminado exitosamente", id);
        } catch (Exception e) {
            logger.error("Error eliminando usuario: {}", e.getMessage());
        }

        return "redirect:/admin";
    }
}
