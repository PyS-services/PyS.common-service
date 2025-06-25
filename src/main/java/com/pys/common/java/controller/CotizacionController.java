package com.pys.common.java.controller;

import com.pys.common.java.service.CotizacionService;
import com.pys.common.kotlin.model.Cotizacion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/common-service/cotizacion")
public class CotizacionController {

    private final CotizacionService service;

    public CotizacionController(CotizacionService service) {
        this.service = service;
    }

    @GetMapping("/")
    public ResponseEntity<List<Cotizacion>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping("/addOrUpdate")
    public ResponseEntity<Cotizacion> addOrUpdate(@RequestBody Cotizacion cotizacion) {
        return ResponseEntity.ok(service.addOrUpdate(cotizacion));
    }

}
