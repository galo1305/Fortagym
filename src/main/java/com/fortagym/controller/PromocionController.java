package com.fortagym.controller;

import com.fortagym.model.Promocion;
import com.fortagym.repository.PromocionRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin/promociones")
public class PromocionController {

    private static final Logger logger = LoggerFactory.getLogger(PromocionController.class);

    private final PromocionRepository promocionRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";


    public PromocionController(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }

    @GetMapping
    public String mostrarPromociones(Model model) {
        logger.info("📢 Mostrando promociones... Total: {}", promocionRepository.count());
        model.addAttribute("promociones", promocionRepository.findAll());
        return "admin_promociones";
    }

    @PostMapping("/subir")
    public String subirPromocion(@RequestParam("nombre") String nombre,
                             @RequestParam("imagen") MultipartFile imagen) throws IOException {

    logger.info("🆙 Subiendo promoción: {}", nombre);

    if (nombre == null || nombre.isBlank()) {
        return "redirect:/admin/promociones";
    }

    if (imagen.isEmpty()) {
        return "redirect:/admin/promociones";
    }

    // Crear carpeta si no existe
    Files.createDirectories(Paths.get(uploadDir));

    // Nombre seguro
    String originalFilename = Paths.get(imagen.getOriginalFilename()).getFileName().toString();
    String fileName = System.currentTimeMillis() + "_" + originalFilename;

    Path destino = Paths.get(uploadDir).resolve(fileName);

    Files.copy(imagen.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

    // URL pública
    String imagePath = "/uploads/" + fileName;

    Promocion promocion = new Promocion(nombre, imagePath);
    promocionRepository.save(promocion);

    logger.info("🎉 Promoción '{}' registrada correctamente.", nombre);

    return "redirect:/admin/promociones";
    }


    @PostMapping("/eliminar/{id}")
    public String eliminarPromocion(@PathVariable Long id) {

        logger.info("🗑 Eliminando promoción ID {}", id);

        Promocion promo = promocionRepository.findById(id).orElse(null);

        if (promo == null) {
            logger.warn("⚠ No existe la promoción con ID {}", id);
            return "redirect:/admin/promociones";
        }

        // Eliminar archivo físico
        if (promo.getImagen() != null) {
            Path imagePath = Paths.get("src/main/resources/static" + promo.getImagen());
            try {
                Files.deleteIfExists(imagePath);
                logger.info("🧹 Imagen eliminada: {}", imagePath.toString());
            } catch (IOException e) {
                logger.error("❌ No se pudo eliminar la imagen: {}", e.getMessage());
            }
        }

        promocionRepository.delete(promo);
        logger.info("✔ Promoción ID {} eliminada.", id);

        return "redirect:/admin/promociones";
    }
}
