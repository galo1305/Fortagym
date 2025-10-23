package com.fortagym.controller;

import com.fortagym.model.Usuario;
import com.fortagym.repository.UsuarioRepository;
import com.fortagym.service.UsuarioService;
import com.fortagym.service.EmailAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String redirigirInicio() {
        return "redirect:/registro";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.registrar(usuario);
            redirectAttributes.addFlashAttribute("registroExitoso", true);
            return "redirect:/login";
        } catch (EmailAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorEmail", e.getMessage());
            redirectAttributes.addFlashAttribute("usuario", usuario);
            return "redirect:/registro";
        }
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @GetMapping("/index")
    public String mostrarIndex(Model model, Principal principal) {
        Usuario usuario= usuarioRepository.findByEmail(principal.getName());
        if(usuario != null){
            model.addAttribute("nombreCompleto", usuario.getNombre()+ " "+ usuario.getApellido());
        }else{
            model.addAttribute("nombreCompleto", "Usuario desconocido");
        }
        
        return "index";
    }

     // --- Perfil de usuario ---
    @GetMapping("/usuario")
    public String mostrarPerfil(Model model, Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName());
        model.addAttribute("usuario", usuario);
        return "usuario";
    }

    // --- Subir foto de perfil ---
    @PostMapping("/usuario/foto")
    public String subirFotoPerfil(@RequestParam("foto") MultipartFile archivo,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {

        if (archivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Selecciona una imagen válida.");
            return "redirect:/usuario";
        }

        try {
            Usuario usuario = usuarioRepository.findByEmail(principal.getName());
            usuario.setFotoPerfil(archivo.getBytes());
            usuarioService.guardar(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Foto actualizada correctamente.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la imagen.");
        }

        return "redirect:/usuario";
    }

    // --- Mostrar la imagen del usuario ---
    @GetMapping("/usuario/foto/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> mostrarFoto(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null || usuario.getFotoPerfil() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(usuario.getFotoPerfil(), headers, HttpStatus.OK);
    }

    @PostMapping("/usuario/cambio")
public String actualizarUsuario(@RequestParam String nombre,
                                @RequestParam String apellido,
                                @RequestParam(required = false) String password,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {

    Usuario usuario = usuarioRepository.findByEmail(principal.getName());
    usuario.setNombre(nombre);
    usuario.setApellido(apellido);

    if (password != null && !password.isBlank()) {
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
    }

    usuarioService.guardar(usuario);
    redirectAttributes.addFlashAttribute("mensaje", "Datos actualizados correctamente.");
    return "redirect:/usuario";
}

}
