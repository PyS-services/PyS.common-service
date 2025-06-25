package com.pys.common.java.controller;

import com.pys.common.java.service.ProveedorService;
import com.pys.common.kotlin.exception.ProveedorException;
import com.pys.common.kotlin.model.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/common-service/proveedor")
public class ProveedorController {

    private final ProveedorService service;

    public ProveedorController(ProveedorService service) {
        this.service = service;
    }

    @GetMapping("/")
    public ResponseEntity<List<Proveedor>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Proveedor>> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.findAllPaginated(pageable));
    }

    @GetMapping("/page/contains/{razonSocial}")
    public ResponseEntity<Page<Proveedor>> findAllPaginatedContaining(
            @PathVariable String razonSocial,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.findAllPaginatedContains(razonSocial, pageable));
    }

    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Proveedor>> searchProveedores(@PathVariable String searchString) {
        return ResponseEntity.ok(service.searchProveedores(searchString));
    }

    @GetMapping("/{proveedorId}")
    public ResponseEntity<Proveedor> findByProveedorId(@PathVariable UUID proveedorId) {
        try {
            return ResponseEntity.ok(service.findByProveedorId(proveedorId));
        } catch (ProveedorException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/negocio/{proveedorIdNegocio}")
    public ResponseEntity<Proveedor> findByProveedorIdNegocio(@PathVariable Long proveedorIdNegocio) {
        try {
            return ResponseEntity.ok(service.findByProveedorIdNegocio(proveedorIdNegocio));
        } catch (ProveedorException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/cuit/{cuit}")
    public ResponseEntity<Proveedor> findByCuit(@PathVariable String cuit) {
        try {
            return ResponseEntity.ok(service.findByCuit(cuit));
        } catch (ProveedorException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/last")
    public ResponseEntity<Proveedor> findLastByProveedorIdNegocio() {
        try {
            return ResponseEntity.ok(service.findLastByProveedorIdNegocio());
        } catch (ProveedorException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<Proveedor> add(@RequestBody Proveedor proveedor) {
        return ResponseEntity.ok(service.add(proveedor));
    }

    @PostMapping("/addOrUpdate")
    public ResponseEntity<Proveedor> addOrUpdate(@RequestBody Proveedor proveedor) {
        return ResponseEntity.ok(service.addOrUpdate(proveedor));
    }

    @PutMapping("/{proveedorId}")
    public ResponseEntity<Proveedor> update(@PathVariable UUID proveedorId, @RequestBody Proveedor proveedor) {
        return ResponseEntity.ok(service.updateByProveedorId(proveedor, proveedorId));
    }

    @DeleteMapping("/{proveedorId}")
    public ResponseEntity<Void> delete(@PathVariable UUID proveedorId) {
        service.deleteByProveedorId(proveedorId);
        return ResponseEntity.noContent().build();
    }

}
