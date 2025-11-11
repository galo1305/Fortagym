package com.fortagym.controller;

import com.fortagym.model.Promocion;
import com.fortagym.repository.PromocionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Controller
@RequestMapping("/admin/promociones")
public class PromocionController {

    private final PromocionRepository promocionRepository;

    public PromocionController(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }

    @GetMapping
    public String mostrarPromociones(Model model) {
        model.addAttribute("promociones", promocionRepository.findAll());
        return "admin_promociones";
    }

    @PostMapping("/subir")
    public String subirPromocion(@RequestParam("nombre") String nombre,
                                 @RequestParam("imagen") MultipartFile imagen) throws IOException {

        if (!imagen.isEmpty()) {
            // Carpeta donde guardar las imágenes (src/main/resources/static/uploads/)
            String uploadDir = "src/main/resources/static/uploads/";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.write(path, imagen.getBytes());

            String imagePath = "/uploads/" + fileName; // ruta accesible desde el navegador

            Promocion promo = new Promocion(nombre, imagePath);
            promocionRepository.save(promo);
        }

        return "redirect:/admin/promociones";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPromocion(@PathVariable Long id) {
        promocionRepository.deleteById(id);
        return "redirect:/admin/promociones";
    }
}
