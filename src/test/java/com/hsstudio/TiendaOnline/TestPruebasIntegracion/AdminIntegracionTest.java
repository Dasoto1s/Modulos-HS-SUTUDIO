package com.hsstudio.TiendaOnline.TestPruebasIntegracion;

import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import com.hsstudio.TiendaOnline.Admin.entidad.Inventario;
import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.InventarioRepositorio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Import(AdminIntegrationTestSecurityConfig.class)
public class AdminIntegracionTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AdminRepositorio adminRepositorio;
    @Autowired
    private InventarioRepositorio inventarioRepositorio;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

   @BeforeEach
public void setUp() {
    // Crear un administrador de prueba
    Admin admin = new Admin();
    admin.setEmail("admin@example.com");
    admin.setPasswordWithHash("password");
    adminRepositorio.save(admin);

    // Crear un inventario asociado al administrador
    Inventario inventario = new Inventario();
    inventario.setAdmin(admin);
    inventarioRepositorio.save(inventario);
}

    @Test
    public void testAdminLogin() throws Exception {
        mockMvc.perform(post("/admin/login")
                .with(httpBasic("admin@example.com", "password"))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("email", "admin@example.com")
                .param("password", "password"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAdminLoginInvalidCredentials() throws Exception {
        mockMvc.perform(post("/admin/login")
                .param("email", "test@admin.com")
                .param("password", "wrongpassword")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAdminLoginMissingCredentials() throws Exception {
        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isUnauthorized());
    }
}