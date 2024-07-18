package com.hsstudio.TiendaOnline.SecurityTest;

import com.hsstudio.TiendaOnline.Admin.controlador.AdminControlador;
import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminLoginSecurityTest {

    private AdminControlador adminControlador;

    @Mock
    private AdminRepositorio adminRepositorio;

    private BCryptPasswordEncoder passwordEncoder;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
        secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        adminControlador = new AdminControlador(adminRepositorio, secretKey, passwordEncoder);
    }

    @Test
    void testLoginSuccessful() {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("password123"));

        when(adminRepositorio.findByEmail("admin@example.com")).thenReturn(admin);

        ResponseEntity<?> response = adminControlador.login("admin@example.com", "password123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody.get("token"));
        assertEquals("Inicio de sesión exitoso", responseBody.get("message"));
    }

    @Test
    void testLoginFailedIncorrectPassword() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("password123"));

        when(adminRepositorio.findByEmail("admin@example.com")).thenReturn(admin);

        ResponseEntity<?> response = adminControlador.login("admin@example.com", "wrongpassword");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Correo electrónico o contraseña incorrectos", response.getBody());
    }

    @Test
    void testLoginFailedNonExistentUser() {
        when(adminRepositorio.findByEmail("nonexistent@example.com")).thenReturn(null);

        ResponseEntity<?> response = adminControlador.login("nonexistent@example.com", "password123");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Correo electrónico o contraseña incorrectos", response.getBody());
    }

    @Test
    void testLoginEmptyCredentials() {
        ResponseEntity<?> response = adminControlador.login("", "");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El correo electrónico y la contraseña son obligatorios", response.getBody());
    }

    @Test
    void testLoginBruteForceProtection() {
        Admin admin = new Admin();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("password123"));

        when(adminRepositorio.findByEmail("admin@example.com")).thenReturn(admin);

        for (int i = 0; i < 5; i++) {
            ResponseEntity<?> response = adminControlador.login("admin@example.com", "wrongpassword");
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }

        ResponseEntity<?> response = adminControlador.login("admin@example.com", "wrongpassword");

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertEquals("Demasiados intentos fallidos. Por favor, intente más tarde.", response.getBody());
    }

    @Test
    void testLoginSQLInjectionAttempt() {
        String sqlInjectionAttempt = "' OR '1'='1";
        
        ResponseEntity<?> response = adminControlador.login(sqlInjectionAttempt, sqlInjectionAttempt);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotEquals("Inicio de sesión exitoso", response.getBody());
    }
}