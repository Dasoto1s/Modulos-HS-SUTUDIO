package com.hsstudio.TiendaOnline.Cliente.TestPruebasUnitarias;

import com.hsstudio.TiendaOnline.Admin.entidad.Inventario;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoComprasDTO;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.controlador.CarritoComprasControlador;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import com.hsstudio.TiendaOnline.Cliente.controlador.CarritoComprasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.hsstudio.TiendaOnline.Admin.entidad.MensajeRespuesta;




@ExtendWith(MockitoExtension.class)
public class CarritoComprasControladorTest {

    @InjectMocks
    private CarritoComprasControlador controlador;

    @Mock
    private CarritoComprasRepositorio carritoComprasRepositorio;

    @Mock
    private ProductoRepositorio productoRepositorio;

    @Mock
    private PromocionesRepositorio promocionesRepositorio;

    @Mock
    private CarritoComprasService carritoComprasService;

    @Mock
    private HttpServletRequest request;

    private final String SESSION_ID = "session123";

    @BeforeEach
    public void setUp() {
        when(request.getHeader("X-Session-Id")).thenReturn(SESSION_ID);
    }

    @Test
public void testObtenerProductosCarrito() {
    CarritoCompras carrito = new CarritoCompras();
    carrito.setIdCarrito(1);
    carrito.setNumeroProductos(2);
    carrito.setPrecioTotal(100.0f);
    carrito.setSessionId(SESSION_ID);
    
    List<Producto> productos = new ArrayList<>();
    Producto producto1 = new Producto();
    producto1.setPrecio(50.0f);
    Producto producto2 = new Producto();
    producto2.setPrecio(50.0f);
    productos.add(producto1);
    productos.add(producto2);
    carrito.setProductos(productos);

    when(carritoComprasRepositorio.findBySessionId(SESSION_ID)).thenReturn(carrito);
    when(promocionesRepositorio.findByProductoIdProducto(any())).thenReturn(Optional.empty());

    ResponseEntity<Object> response = controlador.obtenerProductosCarrito(request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    CarritoComprasDTO carritoDTO = (CarritoComprasDTO) response.getBody();
    assertNotNull(carritoDTO);
    assertEquals(carrito.getIdCarrito(), carritoDTO.getIdCarrito());
    assertEquals(carrito.getNumeroProductos(), carritoDTO.getNumeroProductos());
    assertEquals(carrito.getPrecioTotal(), carritoDTO.getPrecioTotal(), 0.01);
}

   @Test
public void testAgregarProducto() {
    Integer idProducto = 1;
    Producto producto = new Producto();
    producto.setIdProducto(idProducto);
    producto.setPrecio(50.0f);

    Inventario inventario = new Inventario();
    inventario.setCantidad(10);
    inventario.setStock(20);
    inventario.setCantidad_minima_requerida(5);
    producto.setInventario(inventario);

    when(productoRepositorio.findById(idProducto)).thenReturn(Optional.of(producto));
    when(carritoComprasRepositorio.findBySessionId(SESSION_ID)).thenReturn(null);
    when(promocionesRepositorio.findByProductoIdProducto(idProducto)).thenReturn(Optional.empty());

    ResponseEntity<?> response = controlador.agregarProducto(idProducto, request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody() instanceof CarritoComprasDTO);
    CarritoComprasDTO carritoDTO = (CarritoComprasDTO) response.getBody();
    assertNotNull(carritoDTO);
    assertEquals(1, carritoDTO.getNumeroProductos());
    assertEquals(producto.getPrecio(), carritoDTO.getPrecioTotal());
    verify(carritoComprasRepositorio, times(1)).save(any(CarritoCompras.class));
}


    @Test
    public void testEliminarProducto() {
        Integer idProducto = 1;
        CarritoCompras carrito = new CarritoCompras();
        carrito.setIdCarrito(1);
        carrito.setNumeroProductos(2);
        carrito.setPrecioTotal(100.0f);
        carrito.setSessionId(SESSION_ID);

        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        producto.setPrecio(50.0f);

        List<Producto> productos = new ArrayList<>();
        productos.add(producto);
        carrito.setProductos(productos);

        when(carritoComprasRepositorio.findBySessionId(SESSION_ID)).thenReturn(carrito);

       ResponseEntity<CarritoComprasDTO> response = controlador.eliminarProducto(idProducto, request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    CarritoComprasDTO carritoDTO = response.getBody();
    assertNotNull(carritoDTO);
    assertEquals(0, carritoDTO.getNumeroProductos());
    assertEquals(0.0f, carritoDTO.getPrecioTotal(), 0.01);
    verify(carritoComprasRepositorio, times(1)).save(carrito);
}

    @Test
    public void testObtenerProductosCarritoNoEncontrado() {
        when(carritoComprasRepositorio.findBySessionId(SESSION_ID)).thenReturn(null);

        ResponseEntity<Object> response = controlador.obtenerProductosCarrito(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

 

    
}