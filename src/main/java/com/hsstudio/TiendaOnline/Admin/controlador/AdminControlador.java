package com.hsstudio.TiendaOnline.Admin.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/admin")
public class AdminControlador {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminControlador.class);
    
    private final AdminRepositorio adminRepositorio;
    private final SecretKey secretKey;
    private final BCryptPasswordEncoder passwordEncoder;
    
    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    
    @Autowired
    public AdminControlador(AdminRepositorio adminRepositorio, SecretKey secretKey, BCryptPasswordEncoder passwordEncoder) {
        this.adminRepositorio = adminRepositorio;
        this.secretKey = secretKey;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        logger.info("Inicio de sesión de administrador: {}", email);
        
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            logger.warn("Credenciales faltantes para el inicio de sesión de administrador: {}", email);
            return ResponseEntity.badRequest().body("El correo electrónico y la contraseña son obligatorios");
        }
        
        if (loginAttempts.getOrDefault(email, 0) >= MAX_ATTEMPTS) {
            logger.warn("Demasiados intentos fallidos para el inicio de sesión de administrador: {}", email);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Demasiados intentos fallidos. Por favor, intente más tarde.");
        }
        
        Admin existingAdmin = adminRepositorio.findByEmail(email);
        
        if (existingAdmin != null && passwordEncoder.matches(password, existingAdmin.getPassword())) {
            logger.info("Inicio de sesión exitoso para el administrador: {}", email);
            loginAttempts.remove(email);
            String token = Jwts.builder()
                    .setSubject(existingAdmin.getEmail())
                    .claim("adminId", existingAdmin.getId())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Inicio de sesión exitoso");
            response.put("token", token);
            return ResponseEntity.ok().body(response);
        } else {
            logger.warn("Credenciales incorrectas para el inicio de sesión de administrador: {}", email);
            loginAttempts.merge(email, 1, Integer::sum);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo electrónico o contraseña incorrectos");
        }
    }
    
    public Map<String, Integer> getLoginAttempts() {
        return loginAttempts;
    }
}