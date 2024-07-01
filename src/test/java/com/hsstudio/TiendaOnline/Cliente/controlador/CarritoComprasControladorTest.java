package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Inventario;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoComprasDTO;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CarritoComprasControladorTest {

    @InjectMocks
    private CarritoComprasControlador controlador;

    @Mock
    private CarritoComprasRepositorio carritoComprasRepositorio;

    @Mock
    private ProductoRepositorio productoRepositorio;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testObtenerProductosCarrito() {
        String sessionId = "session123";
        CarritoCompras carrito = new CarritoCompras();
        carrito.setIdCarrito(1);
        carrito.setNumeroProductos(2);
        carrito.setPrecioTotal(100.0f);
        carrito.setSessionId(sessionId);

        when(request.getHeader("X-Session-Id")).thenReturn(sessionId);
        when(carritoComprasRepositorio.findBySessionId(sessionId)).thenReturn(carrito);

        ResponseEntity<CarritoComprasDTO> response = controlador.obtenerProductosCarrito(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        CarritoComprasDTO carritoDTO = response.getBody();
        assertNotNull(carritoDTO);
        assertEquals(carrito.getIdCarrito(), carritoDTO.getIdCarrito());
        assertEquals(carrito.getNumeroProductos(), carritoDTO.getNumeroProductos());
        assertEquals(carrito.getPrecioTotal(), carritoDTO.getPrecioTotal());
    }

@Test
public void testAgregarProducto() {
    String sessionId = "session123";
    Integer idProducto = 1;
    Producto producto = new Producto();
    producto.setIdProducto(idProducto);
    producto.setPrecio(50.0f);

    Inventario inventario = new Inventario();
    inventario.setCantidad(10);
    inventario.setStock(20);
    inventario.setCantidad_minima_requerida(5);
    producto.setInventario(inventario);

    when(request.getHeader("X-Session-Id")).thenReturn(sessionId);
    when(productoRepositorio.findById(idProducto)).thenReturn(Optional.of(producto));
    when(carritoComprasRepositorio.findBySessionId(sessionId)).thenReturn(null);

    ResponseEntity<CarritoComprasDTO> response = controlador.agregarProducto(idProducto, request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    CarritoComprasDTO carritoDTO = response.getBody();
    assertNotNull(carritoDTO);
    assertEquals(1, carritoDTO.getNumeroProductos());
    assertEquals(producto.getPrecio(), carritoDTO.getPrecioTotal());
    verify(carritoComprasRepositorio, times(1)).save(any(CarritoCompras.class));
}

    @Test
    public void testEliminarProducto() {
        String sessionId = "session123";
        Integer idProducto = 1;
        CarritoCompras carrito = new CarritoCompras();
        carrito.setIdCarrito(1);
        carrito.setNumeroProductos(2);
        carrito.setPrecioTotal(100.0f);
        carrito.setSessionId(sessionId);

        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        producto.setPrecio(50.0f);

        List<Producto> productos = new ArrayList<>();
        productos.add(producto);
        carrito.setProductos(productos);

        when(request.getHeader("X-Session-Id")).thenReturn(sessionId);
        when(carritoComprasRepositorio.findBySessionId(sessionId)).thenReturn(carrito);

        ResponseEntity<CarritoComprasDTO> response = controlador.eliminarProducto(idProducto, request, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        CarritoComprasDTO carritoDTO = response.getBody();
        assertNotNull(carritoDTO);
        assertEquals(0, carritoDTO.getNumeroProductos());
        assertEquals(0.0f, carritoDTO.getPrecioTotal());
        verify(carritoComprasRepositorio, times(1)).save(carrito);
    }

}