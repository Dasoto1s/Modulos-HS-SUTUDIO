/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.Admin.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.InventarioRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author alexa
 */
public class AdminControlador {
    
        private final ProductoRepositorio productoRepositorio;
        private final InventarioRepositorio inventarioRepositorio;
        private final AdminRepositorio adminRepositorio;

        private final SecretKey secretKey;


        private final PromocionesRepositorio promocionesRepositorio;
      private final ProductoService productoService;



        @Autowired
        public AdminControlador(ProductoRepositorio productoRepositorio,
                                   InventarioRepositorio inventarioRepositorio,
                                   AdminRepositorio adminRepositorio,
                                   PromocionesRepositorio promocionesRepositorio,
                                   ProductoService  productoService) {
            this.productoRepositorio = productoRepositorio;
            this.inventarioRepositorio = inventarioRepositorio;
            this.adminRepositorio = adminRepositorio;
            this.promocionesRepositorio = promocionesRepositorio;
            this.productoService = productoService;
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
          @PostMapping("/admin/login")
        public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
            Admin existingAdmin = adminRepositorio.findByEmail(email);

            if (existingAdmin != null && existingAdmin.getPassword().equals(password)) {
                String token = Jwts.builder()
                        .setSubject(existingAdmin.getEmail())
                        .claim("adminId", existingAdmin.getId())
                        .setIssuedAt(new Date())
                        .signWith(secretKey)
                        .compact();

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Inicio de sesión exitoso");
                response.put("token", token);

                return ResponseEntity.ok().body(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo electrónico o contraseña incorrectos");
            }
        }
}
