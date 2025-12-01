package com.fortagym.controller;

import com.fortagym.model.Usuario;
import com.fortagym.model.Nutricion;
import com.fortagym.model.Rutina;
import com.fortagym.model.DetalleRutina;
import com.fortagym.repository.UsuarioRepository;
import com.fortagym.repository.NutricionRepository;
import com.fortagym.repository.RutinaRepository;
import com.fortagym.repository.DetalleRutinaRepository;
import com.fortagym.service.UsuarioService;
import com.fortagym.service.RutinaService;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/cartilla")
public class CartillaController {

    private static final Logger logger = LoggerFactory.getLogger(CartillaController.class);

    private final UsuarioRepository usuarioRepository;
    private final NutricionRepository nutricionRepository;
    private final RutinaRepository rutinaRepository;
    private final DetalleRutinaRepository detalleRutinaRepository;

    public CartillaController(
            UsuarioRepository usuarioRepository,
            NutricionRepository nutricionRepository,
            RutinaRepository rutinaRepository,
            DetalleRutinaRepository detalleRutinaRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.nutricionRepository = nutricionRepository;
        this.rutinaRepository = rutinaRepository;
        this.detalleRutinaRepository = detalleRutinaRepository;
    }

    // ==============================
    //      VISTA DE CARTILLA
    // ==============================
    @GetMapping("/{idUsuario}")
    public String verCartilla(@PathVariable Long idUsuario, Model model) {

        logger.info("📄 Solicitando cartilla digital del usuario con ID {}", idUsuario);

        // Usuario
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            logger.warn("⚠ Usuario ID {} NO encontrado", idUsuario);
            return "redirect:/";
        }

        logger.info("👤 Usuario: {} {}", usuario.getNombre(), usuario.getApellido());

        // Nutrición
        Nutricion nutricion = nutricionRepository.findByUsuario(usuario).orElse(null);

        // Rutina + detalles
        Rutina rutina = rutinaRepository.findByUsuario(usuario).orElse(null);
        List<DetalleRutina> detalles = Collections.emptyList();

        if (rutina != null) {
            detalles = detalleRutinaRepository.findByRutina(rutina);
            logger.info("🏋️‍♂️ Rutina encontrada con {} ejercicios", detalles.size());
        } else {
            logger.warn("🏋️‍♂️ Usuario ID {} no tiene rutina asignada", idUsuario);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("nutricion", nutricion);
        model.addAttribute("rutina", rutina);
        model.addAttribute("detalles", detalles);

        return "cartilla/cartilla_digital";
    }

    // ==============================
    //      EXPORTAR EXCEL
    // ==============================
    @GetMapping("/exportar/{usuarioId}")
    public void exportarCartillaExcel(@PathVariable Long usuarioId, HttpServletResponse response) throws IOException {

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Nutricion nutricion = nutricionRepository.findByUsuario(usuario).orElse(null);
        Rutina rutina = rutinaRepository.findByUsuario(usuario).orElse(null);
        List<DetalleRutina> detalles = rutina != null
                ? detalleRutinaRepository.findByRutina(rutina)
                : Collections.emptyList();

        // CONFIGURAR DESCARGA
        response.setContentType("application/octet-stream");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=cartilla_" + usuario.getNombre() + ".xlsx"
        );

        Workbook workbook = new XSSFWorkbook();

        // ======================
        // HOJA 1 - NUTRICIÓN
        // ======================
        Sheet sheetNutri = workbook.createSheet("Nutrición");

        Row titulo = sheetNutri.createRow(0);
        titulo.createCell(0).setCellValue("Cartilla Nutricional");

        if (nutricion != null) {
            Row r1 = sheetNutri.createRow(2);
            r1.createCell(0).setCellValue("Análisis Corporal");
            r1.createCell(1).setCellValue(nutricion.getAnalisisCorporal());

            Row r2 = sheetNutri.createRow(3);
            r2.createCell(0).setCellValue("Fecha de Registro");
            r2.createCell(1).setCellValue(nutricion.getFechaRegistro().toString());

            Row r3 = sheetNutri.createRow(4);
            r3.createCell(0).setCellValue("Observaciones");
            r3.createCell(1).setCellValue(nutricion.getObservaciones());
        } else {
            Row row = sheetNutri.createRow(2);
            row.createCell(0).setCellValue("Sin datos nutricionales.");
        }

        // ======================
        // HOJA 2 - RUTINA
        // ======================
        Sheet sheetRutina = workbook.createSheet("Rutina");

        Row tituloR = sheetRutina.createRow(0);
        tituloR.createCell(0).setCellValue("Rutina de Entrenamiento");

        if (rutina != null) {

            Row info = sheetRutina.createRow(2);
            info.createCell(0).setCellValue("Entrenador");
            info.createCell(1).setCellValue(rutina.getNombreEntrenador());

            Row head = sheetRutina.createRow(4);
            head.createCell(0).setCellValue("Ejercicio");
            head.createCell(1).setCellValue("Series / Reps");
            head.createCell(2).setCellValue("Descanso");
            head.createCell(3).setCellValue("Días");

            int rowIdx = 5;

            for (DetalleRutina d : detalles) {
                Row row = sheetRutina.createRow(rowIdx++);
                row.createCell(0).setCellValue(d.getEjercicio());
                row.createCell(1).setCellValue(d.getSeriesReps());
                row.createCell(2).setCellValue(d.getDescanso());
                row.createCell(3).setCellValue(d.getDias());
            }

        } else {
            Row row = sheetRutina.createRow(2);
            row.createCell(0).setCellValue("Sin rutina registrada.");
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
