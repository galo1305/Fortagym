package com.fortagym.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fortagym.model.Membresia;
import com.fortagym.model.Pago;
import com.fortagym.model.Usuario;
import com.fortagym.service.CustomUserDetails;
import com.fortagym.service.PagoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/pago")
public class PagoController {

    private static final Logger logger = LoggerFactory.getLogger(PagoController.class);

    @Autowired
    private PagoService pagoService;

    @PostMapping("/confirmar")
    public String confirmarPago(
            @RequestParam String dni,
            @RequestParam String metodoPago,
            @RequestParam Long membresiaId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        logger.info("💳 Iniciando proceso de pago. Método: {}, Membresía ID: {}, DNI: {}",
                metodoPago, membresiaId, dni);

        // =============================
        // VALIDACIONES
        // =============================

        // Validar login
        if (userDetails == null || userDetails.getUsuario() == null) {
            logger.error("❌ El usuario no está autenticado.");
            redirectAttributes.addFlashAttribute("mensaje", "Debes iniciar sesión antes de pagar.");
            return "redirect:/index";
        }

        // Validar DNI
        if (dni == null || !dni.matches("\\d{8}")) {
            logger.warn("❌ DNI inválido: {}", dni);
            redirectAttributes.addFlashAttribute("mensaje", "El DNI debe tener exactamente 8 dígitos numéricos.");
            return "redirect:/index";
        }

        // Validar método de pago
        if (metodoPago == null || metodoPago.isBlank()) {
            logger.warn("⚠ Método de pago vacío.");
            redirectAttributes.addFlashAttribute("mensaje", "Debe seleccionar un método de pago.");
            return "redirect:/index";
        }

        Usuario usuario = userDetails.getUsuario();
        logger.info("👤 Usuario autenticado: {} {}", usuario.getNombre(), usuario.getApellido());


        // Obtener membresía
        Membresia membresia = pagoService.obtenerMembresiaPorId(membresiaId);
        if (membresia == null) {
            logger.error("❌ Membresía no encontrada con ID {}.", membresiaId);
            redirectAttributes.addFlashAttribute("mensaje", "Membresía no encontrada.");
            return "redirect:/index";
        }

        logger.info("🏷 Membresía encontrada: {} - S/{}", membresia.getDescripcion(), membresia.getPrecio());
        
        // Normalizar método de pago
        metodoPago = metodoPago.trim().toLowerCase();

        // =============================
        // REGISTRAR EL PAGO
        // =============================
        Pago pago = new Pago();
        pago.setDni(dni);
        pago.setMetodoPago(metodoPago);
        pago.setMembresia(membresia);
        pago.setUsuario(usuario);
        pago.setMonto(membresia.getPrecio());

        // según método
        switch (metodoPago) {
            case "tarjeta":
                pago.setEstado("verificado");
                logger.info("Pago con tarjeta → estado 'verificado'");
                break;

            case "presencial":
                pago.setEstado("pendiente");
                logger.info("Pago presencial → estado 'pendiente'");
                break;

            default:
                pago.setEstado("pendiente");
                logger.warn("Método desconocido '{}'. Asignando estado 'pendiente'.", metodoPago);
        }


        pagoService.registrarPago(pago);

        // =============================
        // REDIRECCIÓN SEGÚN MÉTODO
        // =============================

        switch (metodoPago) {
    case "presencial":
        logger.info("Mostrando página de confirmación de pago presencial.");
        model.addAttribute("mensaje", "Acércate al gimnasio para completar el pago.");
        return "confirmacionPresencial";

    case "tarjeta":
        logger.info("Mostrando página de verificación de tarjeta.");
        model.addAttribute("mensaje", "Tu pago con tarjeta está en proceso de verificación.");
        return "confirmacionTarjeta";

    default:
        redirectAttributes.addFlashAttribute("mensaje", "Método de pago no reconocido.");
 
        return "redirect:/index";
}
    }
}
