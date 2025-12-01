package com.fortagym.controller;

import com.fortagym.model.DetalleRutina;
import com.fortagym.model.Rutina;
import com.fortagym.model.Usuario;
import com.fortagym.repository.DetalleRutinaRepository;
import com.fortagym.repository.NutricionRepository;
import com.fortagym.repository.RutinaRepository;
import com.fortagym.service.RutinaService;
import com.fortagym.service.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Controller
@RequestMapping("/rutina")
public class RutinaController {

    private static final Logger logger = LoggerFactory.getLogger(RutinaController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RutinaService rutinaService;

    @Autowired
    private NutricionRepository nutricionRepository;

    @Autowired
    private DetalleRutinaRepository detalleRutinaRepository;


    // --------------------------------------------------------------
    // LISTAR USUARIOS
    // --------------------------------------------------------------
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        logger.info("📌 Listando usuarios para Rutina...");

        List<Usuario> usuarios = usuarioService.obtenerSoloUsuarios();

        Map<Long, Boolean> tieneNutricion = new HashMap<>();
        Map<Long, Boolean> tieneRutina = new HashMap<>();

        for (Usuario usuario : usuarios) {
            boolean nutricion = nutricionRepository.existsByUsuarioId(usuario.getId());
            boolean rutina = rutinaService.buscarPorUsuario(usuario).isPresent();

            tieneNutricion.put(usuario.getId(), nutricion);
            tieneRutina.put(usuario.getId(), rutina);
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("tieneNutricion", tieneNutricion);
        model.addAttribute("tieneRutina", tieneRutina);

        return "rutina/lista_usuarios";
    }


    // --------------------------------------------------------------
    // NUEVA RUTINA
    // --------------------------------------------------------------
    @GetMapping("/nueva/{usuarioId}")
    public String nuevaRutina(@PathVariable Long usuarioId, Model model) {
        logger.info("📌 Nueva rutina para usuario {}", usuarioId);

        Usuario usuario = usuarioService.findById(usuarioId);
        if (usuario == null) {
            logger.error("❌ Usuario {} no encontrado", usuarioId);
            return "redirect:/rutina/usuarios";
        }

        Rutina rutina = new Rutina();
        rutina.setUsuario(usuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("rutina", rutina);

        return "rutina/formulario_rutina";
    }


    // --------------------------------------------------------------
    // GUARDAR RUTINA (CREAR O EDITAR)
    // --------------------------------------------------------------
    @PostMapping("/guardar")
    public String guardarRutina(@RequestParam Long usuarioId,
                                @RequestParam String observaciones,
                                @RequestParam String nombreEntrenador,
                                @RequestParam("ejercicio") List<String> ejercicios,
                                @RequestParam("seriesReps") List<String> seriesReps,
                                @RequestParam("descanso") List<String> descansos,
                                @RequestParam("dias") List<String> dias) {

        logger.info("📌 Guardando rutina para usuario {}", usuarioId);

        Usuario usuario = usuarioService.findById(usuarioId);
        if (usuario == null) {
            logger.warn("❌ Usuario {} no encontrado", usuarioId);
            return "redirect:/rutina/usuarios";
        }

        // Buscar si ya tiene rutina o crear una
        Rutina rutina = rutinaService.buscarPorUsuario(usuario)
                .orElse(new Rutina());

        rutina.setUsuario(usuario);
        rutina.setObservaciones(observaciones);
        rutina.setNombreEntrenador(nombreEntrenador);

        // Limpia detalles viejos (orphanRemoval se encarga)
        rutina.getDetalles().clear();

        for (int i = 0; i < ejercicios.size(); i++) {
            if (ejercicios.get(i) == null || ejercicios.get(i).isBlank())
                continue;

            DetalleRutina detalle = new DetalleRutina();
            detalle.setEjercicio(ejercicios.get(i));
            detalle.setSeriesReps(seriesReps.get(i));
            detalle.setDescanso(descansos.get(i));
            detalle.setDias(dias.get(i));
            detalle.setRutina(rutina);

            rutina.getDetalles().add(detalle);
        }

        rutinaService.guardar(rutina);
        logger.info("✔ Rutina guardada correctamente");

        return "redirect:/rutina/usuarios";
    }


    // --------------------------------------------------------------
    // EDITAR RUTINA
    // --------------------------------------------------------------
    @GetMapping("/editar/{usuarioId}")
    public String editarRutina(@PathVariable Long usuarioId, Model model) {
        logger.info("📌 Editando rutina usuario {}", usuarioId);

        Usuario usuario = usuarioService.findById(usuarioId);
        if (usuario == null) {
            return "redirect:/rutina/usuarios";
        }

        Optional<Rutina> rutinaOpt = rutinaService.buscarPorUsuario(usuario);
        if (rutinaOpt.isEmpty()) {
            logger.warn("⚠ Usuario {} no tiene rutina", usuarioId);
            return "redirect:/rutina/usuarios";
        }

        Rutina rutina = rutinaOpt.get();
        rutina.setDetalles(detalleRutinaRepository.findByRutina(rutina));

        model.addAttribute("usuario", usuario);
        model.addAttribute("rutina", rutina);

        return "rutina/editar_rutina";
    }
}
