package com.pys.common.java.controller.facade;

import com.pys.common.java.service.facade.UserService;
import com.pys.common.kotlin.model.Negocio;
import com.pys.common.kotlin.model.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/common-service/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody UserDto user) {
        return ResponseEntity.ok(service.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<List<Negocio>> login(@RequestBody UserDto user) {
        return ResponseEntity.ok(service.login(user));
    }

    @PostMapping("/change/password")
    public ResponseEntity<Boolean> changePassword(@RequestBody UserDto user) {
        return ResponseEntity.ok(service.changePassword(user));
    }

}
