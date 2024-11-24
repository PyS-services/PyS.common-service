package com.pys.common.java.controller;

import com.pys.common.java.service.ProveedorService;
import com.pys.common.kotlin.model.Proveedor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common-service/proveedor")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping("/{proveedorId}")
    public ResponseEntity<Proveedor> findByProveedorId(Long proveedorId) {
        return ResponseEntity.ok(proveedorService.findByProveedorId(proveedorId));
    }

}
