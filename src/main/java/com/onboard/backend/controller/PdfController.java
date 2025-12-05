package com.onboard.backend.controller;

import com.onboard.backend.service.ContratoAlquilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @Autowired
    private ContratoAlquilerService contratoService;

    @GetMapping("/doble/{idReserva}")
    public void generarContratosAmbos(@PathVariable String idReserva) throws Exception {
        contratoService.generarContratosPdfParaUsuarioYPropietario(idReserva);
    }
}
