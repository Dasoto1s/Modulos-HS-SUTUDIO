package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Cliente.entidad.Promociones;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PromocionesControladorTest {

    @InjectMocks
    private PromocionesControlador promocionesControlador;

    @Mock
    private PromocionesRepositorio promocionesRepositorio;

    @Mock
    private ProductoRepositorio productoRepositorio;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testObtenerProductosEnPromocion() {
        // Crear algunos productos en promoción de prueba
        Producto producto1 = new Producto(1, "Producto 1", "Descripción 1", 100.0f, 42, "Negro", "Hombre", new byte[0], "Casual", null);
        Producto producto2 = new Producto(2, "Producto 2", "Descripción 2", 150.0f, 39, "Rojo", "Mujer", new byte[0], "Formal", null);
        Promociones promocion1 = new Promociones(1, producto1, new BigDecimal("0.2"));
        Promociones promocion2 = new Promociones(2, producto2, new BigDecimal("0.15"));
        List<Promociones> promociones = new ArrayList<>();
        promociones.add(promocion1);
        promociones.add(promocion2);

        // Configurar el comportamiento del repositorio
        when(promocionesRepositorio.findAll()).thenReturn(promociones);

        // Llamar al método del controlador
        List<Promociones> result = promocionesControlador.obtenerProductosEnPromocion();

        // Verificar que el resultado es el esperado
        assertEquals(2, result.size());
        assertEquals("Producto 1", result.get(0).getProducto().getNombre());
        assertEquals("Producto 2", result.get(1).getProducto().getNombre());
    }

    @Test
    public void testAgregarProductoEnPromocion() {
        // Crear un producto de prueba
        Producto producto = new Producto(1, "Producto 1", "Descripción 1", 100.0f, 42, "Negro", "Hombre", new byte[0], "Casual", null);

        // Configurar el comportamiento del repositorio
        when(productoRepositorio.findById(1)).thenReturn(Optional.of(producto));
        when(promocionesRepositorio.save(any(Promociones.class))).thenReturn(new Promociones(1, producto, new BigDecimal("0.2")));

        // Llamar al método del controlador
        ResponseEntity<String> result = promocionesControlador.agregarProductoEnPromocion(1, new BigDecimal("0.2"));

        // Verificar que la respuesta es la esperada
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Producto agregado a promociones", result.getBody());
    }

    @Test
    public void testEliminarProductoEnPromocion() {
        // Crear un producto en promoción de prueba
        Producto producto = new Producto(1, "Producto 1", "Descripción 1", 100.0f, 42, "Negro", "Hombre", new byte[0], "Casual", null);
        Promociones promocion = new Promociones(1, producto, new BigDecimal("0.2"));

        // Configurar el comportamiento del repositorio
        when(promocionesRepositorio.findById(1)).thenReturn(Optional.of(promocion));
        doNothing().when(promocionesRepositorio).delete(promocion);

        // Llamar al método del controlador
        ResponseEntity<String> result = promocionesControlador.eliminarProductoEnPromocion(1);

        // Verificar que la respuesta es la esperada
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Producto eliminado de promociones", result.getBody());
    }
}