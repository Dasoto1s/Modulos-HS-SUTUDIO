package com.hsstudio.TiendaOnline.TestPruebasIntegracion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.Pedido;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PedidoIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private CarritoComprasRepositorio carritoComprasRepositorio;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    private String sessionId;
    private CarritoCompras carrito;
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

        // Crear un carrito de prueba
        carrito = new CarritoCompras();
        carrito.setSessionId(sessionId);
        carrito.setNumeroProductos(1);
        carrito.setPrecioTotal(100.0f);
        carrito.getProductos().add(producto);
        carritoComprasRepositorio.save(carrito);
    }

    @Test
    public void testCrearPedido() throws Exception {
        Map<String, Object> pedidoData = new HashMap<>();
        pedidoData.put("direccionEnvio", "Calle de prueba 123");
        pedidoData.put("departamento", "Departamento de prueba");
        pedidoData.put("ciudad", "Ciudad de prueba");

        MvcResult result = mockMvc.perform(post("/pedidos/crear-pedido")
                .header("X-Session-Id", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoData)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Integer idPedido = objectMapper.readValue(content, Integer.class);

        assertThat(idPedido).isNotNull();

        Pedido pedidoCreado = pedidoRepositorio.findById(idPedido).orElse(null);
        assertThat(pedidoCreado).isNotNull();
        assertThat(pedidoCreado.getDireccionEnvio()).isEqualTo("Calle de prueba 123");
        assertThat(pedidoCreado.getDepartamento()).isEqualTo("Departamento de prueba");
        assertThat(pedidoCreado.getCiudad()).isEqualTo("Ciudad de prueba");
        
        // Compara el ID del carrito en lugar del objeto completo
        assertThat(pedidoCreado.getCarritoCompras().getIdCarrito()).isEqualTo(carrito.getIdCarrito());
        
        // También puedes comparar otros datos relevantes del carrito si es necesario
        assertThat(pedidoCreado.getCarritoCompras().getSessionId()).isEqualTo(sessionId);
        assertThat(pedidoCreado.getCarritoCompras().getNumeroProductos()).isEqualTo(carrito.getNumeroProductos());
        assertThat(pedidoCreado.getCarritoCompras().getPrecioTotal()).isEqualTo(carrito.getPrecioTotal());
    }

    @Test
    public void testSeleccionarMetodoPago() throws Exception {
        // Primero, crear un pedido
        Pedido pedido = new Pedido();
        pedido.setCarritoCompras(carrito);
        pedido.setDireccionEnvio("Calle de prueba 123");
        pedido.setFechaPedido(new Date());
        pedido.setDepartamento("Departamento de prueba");
        pedido.setCiudad("Ciudad de prueba");
        pedido.setPrecioTotal(100.0);
        pedidoRepositorio.save(pedido);

        Map<String, String> pagoData = new HashMap<>();
        pagoData.put("idPedido", pedido.getNumeroPedido().toString());
        pagoData.put("metodoPago", "paypal");

        mockMvc.perform(post("/pedidos/seleccionar-metodo-pago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagoData)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("paypal.com")));
    }

    @Test
    public void testConfirmarPago() throws Exception {
        // Primero, crear un pedido
        Pedido pedido = new Pedido();
        pedido.setCarritoCompras(carrito);
        pedido.setDireccionEnvio("Calle de prueba 123");
        pedido.setFechaPedido(new Date());
        pedido.setDepartamento("Departamento de prueba");
        pedido.setCiudad("Ciudad de prueba");
        pedido.setPrecioTotal(100.0);
        pedidoRepositorio.save(pedido);

        mockMvc.perform(post("/pedidos/confirmar-pago")
                .param("idPedido", pedido.getNumeroPedido().toString())
                .header("X-Session-Id", sessionId))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("http://localhost:3000/informacion-cliente?idPedido=" + pedido.getNumeroPedido())));

        // Verificar que el carrito se ha limpiado
        CarritoCompras carritoActualizado = carritoComprasRepositorio.findBySessionId(sessionId);
        assertThat(carritoActualizado.getNumeroProductos()).isEqualTo(0);
        assertThat(carritoActualizado.getPrecioTotal()).isEqualTo(0.0f);
    }

  @Test
public void testObtenerEstadoPago() throws Exception {
    // Crear un pedido de prueba
    Pedido pedido = new Pedido();
    pedido.setCarritoCompras(carrito);
    pedido.setDireccionEnvio("Calle de prueba 123");
    pedido.setFechaPedido(new Date());
    pedido.setDepartamento("Departamento de prueba");
    pedido.setCiudad("Ciudad de prueba");
    pedido.setPrecioTotal(100.0);
    pedido = pedidoRepositorio.save(pedido);

    MvcResult result = mockMvc.perform(get("/pedidos/estado-pago/" + pedido.getNumeroPedido()))
            .andExpect(status().isOk())
            .andReturn();

    String content = result.getResponse().getContentAsString();
    Map<String, Object> response = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {});

    assertThat(response.get("pagado")).isEqualTo(false);
    assertThat(response.get("estado")).isEqualTo("pendiente");

    Map<String, Object> infoPedido = (Map<String, Object>) response.get("infoPedido");
    assertThat(infoPedido).isNotNull();
    assertThat(infoPedido.get("numeroPedido")).isEqualTo(pedido.getNumeroPedido());
    assertThat(((Number) infoPedido.get("total")).floatValue()).isEqualTo(100.0f); // Conversión a float
    assertThat(infoPedido.get("direccionEnvio")).isEqualTo("Calle de prueba 123");

    List<Map<String, Object>> productos = (List<Map<String, Object>>) infoPedido.get("productos");
    assertThat(productos).isNotEmpty();
    assertThat(productos.get(0).get("nombre")).isEqualTo("Zapato de prueba");
    assertThat(((Number) productos.get(0).get("precio")).floatValue()).isEqualTo(100.0f); // Conversión a float
}

}
