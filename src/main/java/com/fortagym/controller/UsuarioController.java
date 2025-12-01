package com.fortagym.controller;

import com.fortagym.model.Membresia;
import com.fortagym.model.PaseDiario;
import com.fortagym.model.Promocion;
import com.fortagym.model.Usuario;
import com.fortagym.repository.MembresiaRepository;
import com.fortagym.repository.PaseDiarioRepository;
import com.fortagym.repository.PromocionRepository;
import com.fortagym.repository.UsuarioRepository;
import com.fortagym.service.UsuarioService;
import com.fortagym.service.EmailAlreadyExistsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private MembresiaRepository membresiaRepository;

    @Autowired
    private PaseDiarioRepository paseDiarioRepository;


    // =======================
    //  REDIRECCIÓN INICIO
    // =======================
    @GetMapping("/")
    public String redirigirInicio() {
        log.info("Redirigiendo al formulario de registro");
        return "redirect:/registro";
    }


    // =======================
    //      REGISTRO
    // =======================
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario,
                                   RedirectAttributes redirectAttributes) {

        log.info("Intentando registrar usuario con email: {}", usuario.getEmail());

        try {
            usuarioService.registrar(usuario);
            redirectAttributes.addFlashAttribute("registroExitoso", true);
            log.info("Usuario registrado correctamente");
            return "redirect:/login";

        } catch (EmailAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorEmail", e.getMessage());
            redirectAttributes.addFlashAttribute("usuario", usuario);
            log.warn("Error en registro: {}", e.getMessage());
            return "redirect:/registro";
        }
    }


    // =======================
    //        LOGIN
    // =======================
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }


    // =======================
    //        INDEX
    // =======================
    @GetMapping("/index")
    public String mostrarIndex(Model model, Principal principal) {

        Usuario usuario = usuarioRepository.findByEmail(principal.getName());
        if (usuario != null) {
            model.addAttribute("nombreCompleto", usuario.getNombre() + " " + usuario.getApellido());
        } else {
            model.addAttribute("nombreCompleto", "Usuario desconocido");
        }

        Promocion promo = promocionRepository.findTopByOrderByFechaSubidaDesc();
        model.addAttribute("promocion", promo != null ? promo : new Promocion("Sin promociones", null));

        model.addAttribute("membresias", membresiaRepository.findAll());
        model.addAttribute("pasesDiarios", paseDiarioRepository.findAll());

        return "index";
    }


    // =======================
    //       PERFIL USUARIO
    // =======================
    @GetMapping("/usuario")
    public String mostrarPerfil(Model model, Principal principal) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName());
        model.addAttribute("usuario", usuario);
        return "usuario";
    }


    // =======================
    //     FOTO DE PERFIL
    // =======================
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
            log.info("Foto actualizada para {}", principal.getName());

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la imagen.");
            log.error("Error al guardar foto: {}", e.getMessage());
        }

        return "redirect:/usuario";
    }

    @GetMapping("/usuario/foto/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> mostrarFoto(@PathVariable Long id) {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null || usuario.getFotoPerfil() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(usuario.getFotoPerfil());
    }


    // =======================
    //     EDITAR USUARIO
    // =======================
    @PostMapping("/usuario/cambio")
    public String actualizarUsuario(@RequestParam String nombre,
                                    @RequestParam String apellido,
                                    @RequestParam(required = false) String password,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioRepository.findByEmail(principal.getName());

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);

        // Evitamos doble encriptación
        usuarioService.actualizarPassword(usuario, password);

        usuarioService.guardar(usuario);

        redirectAttributes.addFlashAttribute("mensaje", "Datos actualizados correctamente.");
        return "redirect:/usuario";
    }


    // =======================
    //    PÁGINAS INFORMATIVAS
    // =======================
    @GetMapping("/sobreNosotros")
    public String mostrarSobreNosotros() {
        return "sobreNosotros";
    }

    @GetMapping("/preguntas")
    public String mostrarPreguntas() {
        return "preguntas";
    }

    @GetMapping("/testimonios")
    public String mostrarTestimonios() {
        return "testimonios";
    }


    // =======================
    //   RESUMEN DE COMPRAS
    // =======================
    @GetMapping("/resumenCompra/{id}")
    public String mostrarResumenCompra(@PathVariable Long id, Model model, Principal principal) {

        Usuario usuario = usuarioRepository.findByEmail(principal.getName());
        Membresia membresia = membresiaRepository.findById(id).orElse(null);

        if (membresia == null) {
            return "redirect:/index";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("membresia", membresia);

        return "resumenCompra";
    }

    @GetMapping("/resumenPase/{id}")
    public String mostrarResumenPase(@PathVariable Long id, Model model, Principal principal) {

        Usuario usuario = usuarioRepository.findByEmail(principal.getName());
        PaseDiario pase = paseDiarioRepository.findById(id).orElse(null);

        if (pase == null) {
            return "redirect:/index";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("pase", pase);

        return "resumenPase";
    }


    // =======================
    //   SECCIONES ADICIONALES
    // =======================
    @GetMapping("/calendario")
    public String mostrarCalendario() {
        return "calendario";
    }

    @GetMapping("/horarios")
    public String mostrarHorario() {
        return "horarios";
    }

    @GetMapping("/dashboard_admin")
    public String dashboardAdmin() {
        return "dashboard_admin";
    }

    @GetMapping("/admin_promociones")
    public String adminPromociones() {
        return "admin_promociones";
    }

    @GetMapping("/dashboard_entrenador")
    public String dashboardEntrenador() {
        return "dashboard_entrenador";
    }

    @GetMapping("/dashboard_nutricionista")
    public String dashboardNutricionista() {
        return "dashboard_nutricionista";
    }

    @GetMapping("/ejercicios")
    public String ejercicios() {
        return "ejercicios";
    }
    @Controller
    public class AccesoController {

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "accesoDenegado";
    }
    }

}
