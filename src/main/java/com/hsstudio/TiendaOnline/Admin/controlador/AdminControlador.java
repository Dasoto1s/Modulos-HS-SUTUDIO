package com.hsstudio.TiendaOnline.Admin.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Date;

@RestController
@RequestMapping("/admin")
public class AdminControlador {
    private final AdminRepositorio adminRepositorio;

    @Autowired
    public AdminControlador(AdminRepositorio adminRepositorio) {
        this.adminRepositorio = adminRepositorio;
    }
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Admin admin) {
    Admin existingAdmin = adminRepositorio.findByEmail(admin.getEmail());
    if (existingAdmin != null && existingAdmin.getPassword().equals(admin.getPassword())) {
        // Genera la clave segura
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        // Genera el token
        String token = Jwts.builder()
            .setSubject(existingAdmin.getEmail())
            .setIssuedAt(new Date())
            .signWith(key)
            .compact();

        // Devuelve el mensaje de éxito
        return ResponseEntity.ok().body("Inicio de sesión exitoso");
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo electrónico o contraseña incorrectos");
    }
}


    // Otros métodos del controlador...
}
