package com.pys.common.java.controller;

import com.pys.common.java.service.ArticuloService;
import com.pys.common.kotlin.exception.ArticuloException;
import com.pys.common.kotlin.model.Articulo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/common-service/articulo")
public class ArticuloController {

    private final ArticuloService service;

    public ArticuloController(ArticuloService service) {
        this.service = service;
    }

    @GetMapping("/codigo/articulo/{codigoArticulo}")
    public ResponseEntity<Articulo> findByCodigoArticulo(@PathVariable String codigoArticulo) {
        try {
            return ResponseEntity.ok(service.findByCodigoArticulo(codigoArticulo));
        } catch (ArticuloException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/addOrUpdate")
    public ResponseEntity<Articulo> addOrUpdate(@RequestBody Articulo articulo) {
        return ResponseEntity.ok(service.addOrUpdate(articulo));
    }

}
