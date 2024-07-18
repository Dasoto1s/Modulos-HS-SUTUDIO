package com.hsstudio.TiendaOnline.SecurityTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.controlador.CarritoComprasControlador;
import com.hsstudio.TiendaOnline.Cliente.controlador.CarritoComprasService;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoComprasDTO;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoComprasControlador.class)
@Import(SecurityTestConfig.class)
public class CarritoComprasSeguridadTest {

    @MockBean
private CarritoComprasRepositorio carritoComprasRepositorio;

@MockBean
private ProductoRepositorio productoRepositorio;

@MockBean
private CarritoComprasService carritoComprasService;

@MockBean
private PromocionesRepositorio promocionesRepositorio;
    
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    

    private String sessionId;
    private Producto producto;

   @BeforeEach
public void setUp() {
    producto = new Producto();
    producto.setIdProducto(1);
    producto.setNombre("Zapato de prueba");
    producto.setPrecio(100.0f);
    when(productoRepositorio.save(any(Producto.class))).thenReturn(producto);
    
    // Inicializar sessionId con un valor válido
    this.sessionId = "test-session-" + System.currentTimeMillis();
}



   @Test
public void testAccesoNoAutorizadoAgregarProducto() throws Exception {
    mockMvc.perform(post("/carrito-compras/agregar-producto")
            .contentType(MediaType.APPLICATION_JSON)
            .content(producto.getIdProducto().toString()))
            .andExpect(status().isForbidden());
}


   @Test
public void testAccesoNoAutorizadoEliminarProducto() throws Exception {
    mockMvc.perform(delete("/carrito-compras/eliminar-producto/" + producto.getIdProducto()))
            .andExpect(status().isForbidden());
}

@Test
public void testInyeccionSQLEnSessionId() throws Exception {
    String sessionIdMalicioso = "' OR 1=1--";
    mockMvc.perform(get("/carrito-compras/productos")
            .header("X-Session-Id", sessionIdMalicioso))
            .andExpect(status().isUnauthorized());
}
@Test
public void testManipulacionDatosAgregarProducto() throws Exception {
    Integer idProductoInvalido = -1;
    mockMvc.perform(post("/carrito-compras/agregar-producto")
            .header("X-Session-Id", this.sessionId)  // Usa this.sessionId
            .contentType(MediaType.APPLICATION_JSON)
            .content(idProductoInvalido.toString()))
            .andExpect(status().isForbidden());
}

  @Test
public void testAccesoNoAutorizadoLimpiarCarrito() throws Exception {
    mockMvc.perform(post("/carrito-compras/limpiar"))
            .andExpect(status().isForbidden());
}

  
}