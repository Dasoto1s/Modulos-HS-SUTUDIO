package com.hsstudio.TiendaOnline.Cliente.TestPruebasUnitarias;

import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Cliente.controlador.PedidoControlador;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.Pedido;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.TransaccionRepositorio;
import com.hsstudio.TiendaOnline.Cliente.controlador.CarritoComprasService;
import com.paypal.base.rest.APIContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoControladorTest {

    private PedidoControlador pedidoControlador;

    @Mock
    private PedidoRepositorio pedidoRepositorio;

    @Mock
    private CarritoComprasRepositorio carritoComprasRepositorio;

    @Mock
    private TransaccionRepositorio transaccionRepositorio;

    @Mock
    private PromocionesRepositorio promocionesRepositorio;

    @Mock
    private CarritoComprasService carritoComprasService;

    @Mock
    private APIContext apiContext;

    @Mock
    private HttpServletRequest request;

  private class TestPedidoControlador extends PedidoControlador {
    public TestPedidoControlador(PedidoRepositorio pedidoRepositorio, CarritoComprasRepositorio carritoComprasRepositorio,
                                 TransaccionRepositorio transaccionRepositorio, APIContext apiContext, 
                                 CarritoComprasService carritoComprasService, PromocionesRepositorio promocionesRepositorio) {
        super(pedidoRepositorio, carritoComprasRepositorio, transaccionRepositorio, apiContext, 
              carritoComprasService, promocionesRepositorio);
    }

    @Override
    public ResponseEntity<String> iniciarPago(Integer idPedido, APIContext apiContext) {
        return ResponseEntity.ok("https://www.sandbox.paypal.com/checkoutnow?token=1234567890");
    }
}
   

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pedidoControlador = new TestPedidoControlador(pedidoRepositorio, carritoComprasRepositorio,
                                                      transaccionRepositorio, apiContext, 
                                                      carritoComprasService, promocionesRepositorio);
    }
@Test
void testCrearPedido() {
    // Configurar datos de prueba
    String sessionId = "test-session-id";
    CarritoCompras carrito = new CarritoCompras();
    carrito.setSessionId(sessionId);
    carrito.setPrecioTotal(100.0f);

    Producto producto = new Producto();
    producto.setIdProducto(1);
    producto.setPrecio(100.0f);
    List<Producto> productos = Collections.singletonList(producto);
    carrito.setProductos(productos);

    Map<String, Object> pedidoData = new HashMap<>();
    pedidoData.put("direccionEnvio", "Calle Test 123");
    pedidoData.put("departamento", "Departamento Test");
    pedidoData.put("ciudad", "Ciudad Test");

    when(request.getHeader("X-Session-Id")).thenReturn(sessionId);
    when(carritoComprasRepositorio.findBySessionId(sessionId)).thenReturn(carrito);
    when(pedidoRepositorio.save(any(Pedido.class))).thenAnswer(invocation -> {
        Pedido pedidoGuardado = invocation.getArgument(0);
        pedidoGuardado.setNumeroPedido(1);
        return pedidoGuardado;
    });

    // Ejecutar el método a probar
    ResponseEntity<?> response = pedidoControlador.crearPedido(pedidoData, request);

    // Verificaciones
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody());
    verify(pedidoRepositorio).save(any(Pedido.class));
}

    @Test
    void testSeleccionarMetodoPago() {
        // Configurar datos de prueba
        Map<String, String> pagoData = new HashMap<>();
        pagoData.put("idPedido", "1");
        pagoData.put("metodoPago", "paypal");

        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(1);
        pedido.setPrecioTotal(100.0);
        pedido.setTotalConDescuento(90.0);

        when(pedidoRepositorio.findById(eq(1))).thenReturn(Optional.of(pedido));

        // Ejecutar el método a probar
        ResponseEntity<String> response = pedidoControlador.seleccionarMetodoPago(pagoData);

        // Verificaciones
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("paypal.com"));
    }

    @Test
    void testConfirmarPago() {
        // Configurar datos de prueba
        String idPedido = "1";
        String sessionId = "test-session-id";

        // Ejecutar el método a probar
        ResponseEntity<?> response = pedidoControlador.confirmarPago(idPedido, sessionId);

        // Verificaciones
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("informacion-cliente"));
        verify(carritoComprasService).limpiarCarrito(sessionId);
    }

 @Test
void testObtenerEstadoPago() {
    // Configurar datos de prueba
    Integer idPedido = 1;
    Pedido pedido = new Pedido();
    pedido.setNumeroPedido(idPedido);
    pedido.setPrecioTotal(100.0);
    pedido.setDireccionEnvio("Calle Test 123");

    CarritoCompras carrito = new CarritoCompras();
    Producto producto = new Producto();
    producto.setNombre("Producto Test");
    producto.setPrecio(100.0f);
    carrito.setProductos(Collections.singletonList(producto));
    pedido.setCarritoCompras(carrito);

    when(pedidoRepositorio.findById(idPedido)).thenReturn(Optional.of(pedido));
    when(transaccionRepositorio.findByPedido(pedido)).thenReturn(null);

    // Ejecutar el método a probar
    ResponseEntity<?> response = pedidoControlador.obtenerEstadoPago(idPedido);

    // Verificaciones
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody() instanceof Map);
    Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
    assertFalse((Boolean) responseBody.get("pagado"));
    assertEquals("pendiente", responseBody.get("estado"));
}
}