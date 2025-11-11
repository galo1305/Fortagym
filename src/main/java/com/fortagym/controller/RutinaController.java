package com.fortagym.controller;

import com.fortagym.model.DetalleRutina;
import com.fortagym.model.Rutina;
import com.fortagym.model.Usuario;
import com.fortagym.repository.DetalleRutinaRepository;
import com.fortagym.repository.NutricionRepository;
import com.fortagym.repository.RutinaRepository;
import com.fortagym.repository.UsuarioRepository;
import com.fortagym.service.RutinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fortagym.service.UsuarioService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/rutina")
public class RutinaController {

    @Autowired
    private RutinaRepository rutinaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DetalleRutinaRepository detalleRutinaRepository;

    @Autowired NutricionRepository nutricionRepository;

    @Autowired 
    private UsuarioService usuarioService;

    @Autowired
    private RutinaService rutinaService;


    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
    List<Usuario> usuarios = usuarioService.obtenerTodos();

    Map<Long, Boolean> tieneNutricion = new HashMap<>();
    Map<Long, Boolean> tieneRutina = new HashMap<>();

    for (Usuario usuario : usuarios) {
        // ✅ Consulta real a la BD
        boolean nutricion = nutricionRepository.existsByUsuarioId(usuario.getId());
        boolean rutina = rutinaRepository.findByUsuario(usuario).isPresent();

        tieneNutricion.put(usuario.getId(), nutricion);
        tieneRutina.put(usuario.getId(), rutina);
    }

    model.addAttribute("usuarios", usuarios);
    model.addAttribute("tieneNutricion", tieneNutricion);
    model.addAttribute("tieneRutina", tieneRutina);

    return "rutina/lista_usuarios";
}


    @GetMapping("/nueva/{usuarioId}")
    public String nuevaRutina(@PathVariable Long usuarioId, Model model) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

    Rutina rutina = new Rutina();     // 👈 crea una nueva rutina vacía
    rutina.setUsuario(usuario);       // 👈 asóciala al usuario

    model.addAttribute("usuario", usuario);
    model.addAttribute("rutina", rutina); // 👈 añade al modelo

    return "rutina/formulario_rutina";
}


    @PostMapping("/guardar")
    public String guardarRutina(@RequestParam Long usuarioId,
                            @RequestParam String observaciones,
                            @RequestParam String nombreEntrenador,
                            @RequestParam("ejercicio") List<String> ejercicios,
                            @RequestParam("seriesReps") List<String> seriesReps,
                            @RequestParam("descanso") List<String> descansos,
                            @RequestParam("dias") List<String> dias) {

    Usuario usuario = usuarioService.findById(usuarioId);
    if (usuario == null) {
        return "redirect:/rutina/usuarios";
    }

    // Buscar si ya tiene rutina
    Rutina rutina = rutinaService.buscarPorUsuario(usuario)
            .orElseGet(() -> new Rutina());

    rutina.setUsuario(usuario);
    rutina.setObservaciones(observaciones);
    rutina.setNombreEntrenador(nombreEntrenador);

    // 👇 EN LUGAR DE reemplazar la lista...
    rutina.getDetalles().clear(); // Limpia los existentes (sin perder la referencia)

    // 👇 Luego agregas los nuevos detalles
    for (int i = 0; i < ejercicios.size(); i++) {
        if (ejercicios.get(i).isBlank()) continue; // Evita filas vacías

        DetalleRutina detalle = new DetalleRutina();
        detalle.setEjercicio(ejercicios.get(i));
        detalle.setSeriesReps(seriesReps.get(i));
        detalle.setDescanso(descansos.get(i));
        detalle.setDias(dias.get(i));
        detalle.setRutina(rutina);

        rutina.getDetalles().add(detalle);
    }

    rutinaService.guardar(rutina);
    return "redirect:/rutina/usuarios";
}


    @GetMapping("/editar/{usuarioId}")
    public String editarRutina(@PathVariable Long usuarioId, Model model) {
    Usuario usuario = usuarioService.findById(usuarioId);
    if (usuario == null) {
        return "redirect:/rutina/usuarios";
    }

    Optional<Rutina> rutinaOpt = rutinaService.buscarPorUsuario(usuario);
    if (rutinaOpt.isEmpty()) {
        return "redirect:/rutina/usuarios";
    }

    Rutina rutina = rutinaOpt.get();

    // 🔹 Asegurarse de que los detalles estén cargados
    rutina.setDetalles(detalleRutinaRepository.findByRutina(rutina));

    model.addAttribute("usuario", usuario);
    model.addAttribute("rutina", rutina);

    return "rutina/editar_rutina";
    }

}
