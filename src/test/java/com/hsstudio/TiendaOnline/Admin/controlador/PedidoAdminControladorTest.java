package com.hsstudio.TiendaOnline.Admin.controlador;

import com.hsstudio.TiendaOnline.Admin.dto.PedidoDTO;
import com.hsstudio.TiendaOnline.Cliente.entidad.Pedido;
import com.hsstudio.TiendaOnline.Cliente.entidad.Cliente;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.repositorio.ClienteRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoAdminControladorTest {

    @InjectMocks
    private PedidoAdminControlador pedidoAdminControlador;

    @Mock
    private PedidoRepositorio pedidoRepositorio;

    @Mock
    private ClienteRepositorio clienteRepositorio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

   @Test
void obtenerTodosLosPedidos() {
    // Arrange
    CarritoCompras carrito1 = new CarritoCompras();
    carrito1.setNumeroProductos(2);
    carrito1.setPrecioTotal(100.0f);
    carrito1.setSessionId("session123");

    CarritoCompras carrito2 = new CarritoCompras();
    carrito2.setNumeroProductos(1);
    carrito2.setPrecioTotal(50.0f);
    carrito2.setSessionId("session456");

    Pedido pedido1 = new Pedido();
    pedido1.setNumeroPedido(1);
    pedido1.setCarritoCompras(carrito1);

    Pedido pedido2 = new Pedido();
    pedido2.setNumeroPedido(2);
    pedido2.setCarritoCompras(carrito2);

    List<Pedido> pedidos = Arrays.asList(pedido1, pedido2);

    when(pedidoRepositorio.findAll()).thenReturn(pedidos);

    // Act
    List<PedidoDTO> result = pedidoAdminControlador.obtenerTodosLosPedidos();

    // Assert
    assertEquals(2, result.size());
    assertEquals(1, result.get(0).getNumeroPedido());
    assertEquals(2, result.get(0).getNumeroProductos());
    assertEquals(2, result.get(1).getNumeroPedido());
    assertEquals(1, result.get(1).getNumeroProductos());
}

    @Test
    void obtenerPedidoPorId() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(1);
        pedido.setDireccionEnvio("Calle 123");
        pedido.setFechaPedido(new Date());
        
        CarritoCompras carrito = new CarritoCompras();
        carrito.setNumeroProductos(2);
        carrito.setPrecioTotal(100.0f);
        carrito.setSessionId("session123");
        pedido.setCarritoCompras(carrito);

        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setCorreo("juan@example.com");

        when(pedidoRepositorio.findById(1)).thenReturn(Optional.of(pedido));
        when(clienteRepositorio.findBySessionId("session123")).thenReturn(cliente);

        // Act
        PedidoDTO result = pedidoAdminControlador.obtenerPedidoPorId(1);

        // Assert
        assertEquals(1, result.getNumeroPedido());
        assertEquals("Calle 123", result.getDireccionEnvio());
        assertEquals(2, result.getNumeroProductos());
        assertEquals(100.0f, result.getPrecioTotal());
        assertEquals("Juan", result.getNombreCliente());
        assertEquals("juan@example.com", result.getCorreoCliente());
    }

    @Test
    void actualizarEstadoPedido_EstadoValido() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(1);
        when(pedidoRepositorio.findById(1)).thenReturn(Optional.of(pedido));

        // Act
        ResponseEntity<?> response = pedidoAdminControlador.actualizarEstadoPedido(1, "1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("1", pedido.getEstado_solicitud());
        verify(pedidoRepositorio).save(pedido);
    }

    @Test
    void actualizarEstadoPedido_EstadoInvalido() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido(1);
        when(pedidoRepositorio.findById(1)).thenReturn(Optional.of(pedido));

        // Act
        ResponseEntity<?> response = pedidoAdminControlador.actualizarEstadoPedido(1, "2");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(pedido.getEstado_solicitud());
        verify(pedidoRepositorio, never()).save(pedido);
    }

    @Test
    void actualizarEstadoPedido_PedidoNoEncontrado() {
        // Arrange
        when(pedidoRepositorio.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            pedidoAdminControlador.actualizarEstadoPedido(1, "1");
        });
    }
}