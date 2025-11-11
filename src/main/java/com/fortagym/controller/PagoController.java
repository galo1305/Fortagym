package com.fortagym.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fortagym.model.Membresia;
import com.fortagym.model.Pago;
import com.fortagym.model.Usuario;
import com.fortagym.service.CustomUserDetails;
import com.fortagym.service.PagoService;



@Controller
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping("/confirmar")
    public String confirmarPago(
        @RequestParam String dni,
        @RequestParam String metodoPago,
        @RequestParam Long membresiaId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        Model model
    )   {
    Usuario usuario = userDetails.getUsuario();

    // Recuperar la membresía REAL desde la BD
    Membresia membresia = pagoService.obtenerMembresiaPorId(membresiaId);

    Pago pago = new Pago();
    pago.setDni(dni);
    pago.setMetodoPago(metodoPago);
    pago.setMembresia(membresia);
    pago.setUsuario(usuario);
    pago.setMonto(membresia.getPrecio());

    if (metodoPago.equals("yape")) {
        pago.setEstado("por_verificar");
    } else if (metodoPago.equals("presencial")) {
        pago.setEstado("pendiente");
    }

    pagoService.registrarPago(pago);

    if (metodoPago.equals("presencial")) {
        model.addAttribute("mensaje", "Acércate al gimnasio para completar el pago.");
        return "confirmacionPresencial";
    }

    if (metodoPago.equals("yape")) {
        model.addAttribute("mensaje", "Tu pago por Yape está en proceso de verificación.");
        return "confirmacionYape";
    }

    return "redirect:/index";
    }
}

