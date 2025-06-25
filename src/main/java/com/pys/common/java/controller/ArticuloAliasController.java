package com.pys.common.java.controller;

import com.pys.common.java.service.ArticuloAliasService;
import com.pys.common.kotlin.model.ArticuloAlias;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common-service/articuloAlias")
public class ArticuloAliasController {

    private final ArticuloAliasService service;

    public ArticuloAliasController(ArticuloAliasService service) {
        this.service = service;
    }

    @PostMapping("/addOrUpdate")
    public ResponseEntity<ArticuloAlias> addOrUpdate(@RequestBody ArticuloAlias articuloAlias) {
        return ResponseEntity.ok(service.addOrUpdate(articuloAlias));
    }

}
