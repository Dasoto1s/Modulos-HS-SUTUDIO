package com.hsstudio.TiendaOnline.Admin.TestPruebasUnitarias;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hsstudio.TiendaOnline.Admin.controlador.AdminControlador;
import com.hsstudio.TiendaOnline.Admin.controlador.ProductoService;
import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.InventarioRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class AdminControladorTest {
    @InjectMocks
    private AdminControlador adminControlador;

    @Mock
    private ProductoRepositorio productoRepositorio;

    @Mock
    private InventarioRepositorio inventarioRepositorio;

    @Mock
    private AdminRepositorio adminRepositorio;

    @Mock
    private PromocionesRepositorio promocionesRepositorio;

    @Mock
    private ProductoService productoService;

    @Mock
    private SecretKey secretKey;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

   @BeforeEach
void setUp() {
    MockitoAnnotations.openMocks(this);
    secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    adminControlador = new AdminControlador(adminRepositorio, secretKey, passwordEncoder);
}

    @Test
    void testLoginWithInvalidCredentials() {
        String email = "admin@example.com";
        String password = "wrongpassword";
        when(adminRepositorio.findByEmail(email)).thenReturn(null);
        ResponseEntity<?> response = adminControlador.login(email, password);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Correo electrónico o contraseña incorrectos", response.getBody());
    }

    @Test
    void testLogin() {
        String email = "admin@example.com";
        String password = "password";
        String hashedPassword = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG"; // BCrypt hash for "password"

        Admin admin = new Admin();
        admin.setId(1);
        admin.setEmail(email);
        admin.setPassword(hashedPassword);

        when(adminRepositorio.findByEmail(email)).thenReturn(admin);
        when(passwordEncoder.matches(eq(password), any())).thenReturn(true);

       ResponseEntity<?> response = adminControlador.login(email, password);
    
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertNotNull(responseBody);
    assertEquals("Inicio de sesión exitoso", responseBody.get("message"));
    assertNotNull(responseBody.get("token"));
    }
}