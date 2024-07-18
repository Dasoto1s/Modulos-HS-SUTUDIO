package com.hsstudio.TiendaOnline.TestPruebasIntegracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoComprasDTO;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CarritoComprasIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarritoComprasRepositorio carritoComprasRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    private String sessionId;
    private Producto producto;

    @BeforeEach
    public void setUp() {
        // Crear un producto de prueba
        producto = new Producto();
        producto.setNombre("Zapato de prueba");
        producto.setPrecio(100.0f);
        productoRepositorio.save(producto);

        // Generar un sessionId único para cada prueba
        sessionId = "test-session-" + System.currentTimeMillis();
    }

    @Test
    public void testAgregarProductoAlCarrito() throws Exception {
        MvcResult result = mockMvc.perform(post("/carrito-compras/agregar-producto")
                .header("X-Session-Id", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(producto.getIdProducto().toString()))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        CarritoComprasDTO carritoDTO = objectMapper.readValue(content, CarritoComprasDTO.class);

        assertThat(carritoDTO).isNotNull();
        assertThat(carritoDTO.getNumeroProductos()).isEqualTo(1);
        assertThat(carritoDTO.getPrecioTotal()).isEqualTo(100.0f);
    }

    @Test
    public void testObtenerProductosCarrito() throws Exception {
        // Primero, agregar un producto al carrito
        mockMvc.perform(post("/carrito-compras/agregar-producto")
                .header("X-Session-Id", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(producto.getIdProducto().toString()))
                .andExpect(status().isOk());

        // Luego, obtener los productos del carrito
        MvcResult result = mockMvc.perform(get("/carrito-compras/productos")
                .header("X-Session-Id", sessionId))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        CarritoComprasDTO carritoDTO = objectMapper.readValue(content, CarritoComprasDTO.class);

        assertThat(carritoDTO).isNotNull();
        assertThat(carritoDTO.getProductos()).hasSize(1);
        assertThat(carritoDTO.getProductos().get(0).getNombre()).isEqualTo("Zapato de prueba");
    }

    @Test
    public void testEliminarProductoDelCarrito() throws Exception {
        // Primero, agregar un producto al carrito
        mockMvc.perform(post("/carrito-compras/agregar-producto")
                .header("X-Session-Id", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(producto.getIdProducto().toString()))
                .andExpect(status().isOk());

        // Luego, eliminar el producto del carrito
        MvcResult result = mockMvc.perform(delete("/carrito-compras/eliminar-producto/" + producto.getIdProducto())
                .header("X-Session-Id", sessionId))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        CarritoComprasDTO carritoDTO = objectMapper.readValue(content, CarritoComprasDTO.class);

        assertThat(carritoDTO).isNotNull();
        assertThat(carritoDTO.getNumeroProductos()).isEqualTo(0);
        assertThat(carritoDTO.getPrecioTotal()).isEqualTo(0.0f);
    }

    @Test
    public void testLimpiarCarrito() throws Exception {
        // Primero, agregar un producto al carrito
        mockMvc.perform(post("/carrito-compras/agregar-producto")
                .header("X-Session-Id", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(producto.getIdProducto().toString()))
                .andExpect(status().isOk());

        // Luego, limpiar el carrito
        mockMvc.perform(post("/carrito-compras/limpiar")
                .header("X-Session-Id", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Carrito limpiado con éxito"));

        // Verificar que el carrito esté vacío
        CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(sessionId);
        assertThat(carrito).isNotNull();
        assertThat(carrito.getNumeroProductos()).isEqualTo(0);
        assertThat(carrito.getPrecioTotal()).isEqualTo(0.0f);
        assertThat(carrito.getProductos()).isEmpty();
    }
}
