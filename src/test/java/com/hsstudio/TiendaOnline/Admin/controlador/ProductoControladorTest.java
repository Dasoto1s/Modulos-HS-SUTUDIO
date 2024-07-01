package com.hsstudio.TiendaOnline.Admin.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.*;
import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.InventarioRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoControladorTest {

    @InjectMocks
    private ProductoControlador productoControlador;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void obtenerTodos() {
        Producto producto1 = new Producto();
        producto1.setIdProducto(1);
        producto1.setNombre("Zapato 1");
        
        Producto producto2 = new Producto();
        producto2.setIdProducto(2);
        producto2.setNombre("Zapato 2");

        when(productoRepositorio.findAll()).thenReturn(Arrays.asList(producto1, producto2));

        List<ProductoDTO> result = productoControlador.obtenerTodos();

        assertEquals(2, result.size());
        assertEquals("Zapato 1", result.get(0).getNombre());
        assertEquals("Zapato 2", result.get(1).getNombre());
    }

    @Test
    void crearProducto() throws Exception {
        ProductoControlador spyController = spy(productoControlador);
        doReturn(1).when(spyController).extraerAdminIdDesdeToken(anyString());

        String authHeader = "Bearer validToken";
        MockMultipartFile imagen = new MockMultipartFile("imagen", "test.jpg", "image/jpeg", "test image content".getBytes());

        Admin admin = new Admin();
        admin.setId(1);

        when(adminRepositorio.findById(1)).thenReturn(Optional.of(admin));
        when(productoRepositorio.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto savedProducto = invocation.getArgument(0);
            savedProducto.setIdProducto(1);
            return savedProducto;
        });

        ResponseEntity<Object> response = spyController.crearProducto(
                authHeader, "Zapato Test", "Descripción", 100.0f, "42", "Negro", "Masculino", "Casual", 
                imagen, 10, 20, 5
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(productoRepositorio, times(1)).save(any(Producto.class));
    }

    @Test
    void actualizarProducto() throws Exception {
        Producto productoExistente = new Producto();
        productoExistente.setIdProducto(1);
        productoExistente.setNombre("Zapato Viejo");
        productoExistente.setInventario(new Inventario());

        when(productoRepositorio.findById(1)).thenReturn(Optional.of(productoExistente));
        when(productoRepositorio.save(any(Producto.class))).thenReturn(productoExistente);

        ResponseEntity<Producto> response = productoControlador.actualizarProducto(
                1, "Zapato Nuevo", null, null, null, null, null, null, null, null, null, null
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Zapato Nuevo", response.getBody().getNombre());
    }

    @Test
    void eliminarProducto() {
        ResponseEntity<?> response = productoControlador.eliminarProducto(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productoService, times(1)).eliminarProducto(1);
    }

    @Test
    void login() {
        Admin admin = new Admin();
        admin.setEmail("admin@test.com");
        admin.setPassword("password");
        admin.setId(1);

        when(adminRepositorio.findByEmail("admin@test.com")).thenReturn(admin);

        ResponseEntity<?> response = productoControlador.login("admin@test.com", "password");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void buscarProductos() {
        Producto producto = new Producto();
        producto.setIdProducto(1);
        producto.setNombre("Zapato Test");

        when(productoRepositorio.findByNombreContainingIgnoreCaseAndTalla("Zapato", 42))
                .thenReturn(Arrays.asList(producto));

        ResponseEntity<Object> response = productoControlador.buscarProductos("Zapato", 42, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        List<?> resultList = (List<?>) response.getBody();
        assertEquals(1, resultList.size());
    }
}