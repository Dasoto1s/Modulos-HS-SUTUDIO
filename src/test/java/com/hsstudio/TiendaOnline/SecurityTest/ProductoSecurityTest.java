package com.hsstudio.TiendaOnline.SecurityTest;

import com.hsstudio.TiendaOnline.Admin.controlador.ProductoControlador;
import com.hsstudio.TiendaOnline.Admin.controlador.AdminControlador;
import com.hsstudio.TiendaOnline.Admin.controlador.ProductoService;
import com.hsstudio.TiendaOnline.Admin.entidad.*;
import com.hsstudio.TiendaOnline.Admin.repositorio.*;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityTestConfig.class, TestSecurityConfig.class})
public class ProductoSecurityTest {

    @InjectMocks
    private ProductoControlador productoControlador;

    @Mock
    private AdminControlador adminControlador;

    @Mock
    private ProductoRepositorio productoRepositorio;

    @Mock
    private AdminRepositorio adminRepositorio;

    @Mock
    private InventarioRepositorio inventarioRepositorio;

    @Mock
    private PromocionesRepositorio promocionesRepositorio;

    @Mock
    private ProductoService productoService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productoControlador = new ProductoControlador(productoRepositorio, inventarioRepositorio, adminRepositorio, promocionesRepositorio, productoService);
    }
private String obtenerTokenValido() {
    Admin admin = new Admin();
    admin.setId(1);
    admin.setEmail("test@admin.com");
    admin.setPassword(passwordEncoder.encode("password"));

    when(adminRepositorio.findByEmail("test@admin.com")).thenReturn(admin);

    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("message", "Inicio de sesión exitoso");
    responseBody.put("token", "fake_token_for_test");
    
    ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(responseBody);
    
    // Usamos doReturn().when() en lugar de when().thenReturn()
    doReturn(responseEntity).when(adminControlador).login(anyString(), anyString());

    ResponseEntity<?> loginResponse = adminControlador.login("test@admin.com", "password");
    Map<String, Object> actualResponseBody = (Map<String, Object>) loginResponse.getBody();
    return (String) actualResponseBody.get("token");
}

    @Test
    void testCrearProductoSinAutorizacion() throws Exception {
        ResponseEntity<Object> response = productoControlador.crearProducto(
            null, "Zapato", "Descripción", 100.0f, "42", "Negro", "Masculino", "Casual", 
            new MockMultipartFile("imagen", new byte[0]), 10, 20, 5
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

   

    @Test
    void testActualizarProductoNoExistente() throws Exception {
        when(productoRepositorio.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<Producto> response = productoControlador.actualizarProducto(
            1, "Nuevo Zapato", null, null, null, null, null, null, null, null, null, null
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testEliminarProducto() {
        doNothing().when(productoService).eliminarProducto(1);

        ResponseEntity<?> response = productoControlador.eliminarProducto(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Producto eliminado con éxito", response.getBody());
    }

@Test
void testBuscarProductosConSQLInjection() {
    String sqlInjection = "'; DROP TABLE Producto; --";
    
    when(productoRepositorio.buscarPorNombreIgnoreCaseNativo(sqlInjection)).thenReturn(new ArrayList<>());

    ResponseEntity<Object> response = productoControlador.buscarProductos(sqlInjection, null, null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    
    assertTrue(response.getBody() instanceof MensajeRespuesta, "El cuerpo de la respuesta debería ser un MensajeRespuesta");
    MensajeRespuesta mensajeRespuesta = (MensajeRespuesta) response.getBody();
    assertEquals("No se encontraron productos para la búsqueda especificada.", mensajeRespuesta.getMensaje());
}

    @Test
    void testObtenerTodosLosProductos() {
        Producto producto1 = new Producto();
        producto1.setIdProducto(1);
        producto1.setNombre("Zapato 1");
        Inventario inventario1 = new Inventario();
        inventario1.setCantidad(10f);
        inventario1.setStock(20);
        inventario1.setCantidad_minima_requerida(5);
        producto1.setInventario(inventario1);

        Producto producto2 = new Producto();
        producto2.setIdProducto(2);
        producto2.setNombre("Zapato 2");
        Inventario inventario2 = new Inventario();
        inventario2.setCantidad(15f);
        inventario2.setStock(25);
        inventario2.setCantidad_minima_requerida(8);
        producto2.setInventario(inventario2);

        when(productoRepositorio.findAll()).thenReturn(Arrays.asList(producto1, producto2));

        List<ProductoDTO> result = productoControlador.obtenerTodos();

        assertEquals(2, result.size());
        assertEquals("Zapato 1", result.get(0).getNombre());
        assertEquals("Zapato 2", result.get(1).getNombre());
        assertEquals(10f, result.get(0).getCantidad());
        assertEquals(20, result.get(0).getStock());
        assertEquals(5, result.get(0).getCantidadMinimaRequerida());
        assertEquals(15f, result.get(1).getCantidad());
        assertEquals(25, result.get(1).getStock());
        assertEquals(8, result.get(1).getCantidadMinimaRequerida());
    }
}