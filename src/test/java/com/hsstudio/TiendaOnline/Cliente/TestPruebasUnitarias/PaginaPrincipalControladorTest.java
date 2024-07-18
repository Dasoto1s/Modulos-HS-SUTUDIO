package com.hsstudio.TiendaOnline.Cliente.TestPruebasUnitarias;

import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.controlador.PaginaPrincipalControlador;
import com.hsstudio.TiendaOnline.Cliente.entidad.Promociones;
import com.hsstudio.TiendaOnline.Cliente.repositorio.DestacadosRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaginaPrincipalControladorTest {

    @InjectMocks
    private PaginaPrincipalControlador controlador;

    @Mock
    private ProductoRepositorio productoRepositorio;

    @Mock
    private DestacadosRepositorio destacadosRepositorio;

    @Mock
    private PromocionesRepositorio promocionesRepositorio;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(controlador, "promocionesRepositorio", promocionesRepositorio);
    }

    private Producto crearProductoPrueba(Integer id, String nombre, float precio, int talla, String color, String genero, String tipoZapato) {
        return new Producto(id, nombre, "Descripción de " + nombre, precio, talla, color, genero, new byte[0], tipoZapato, null);
    }

    @Test
    public void testObtenerProductosPorGenero() {
        String genero = "Hombre";
        List<Producto> productosMock = Arrays.asList(
            crearProductoPrueba(1, "Zapato Hombre", 100.0f, 42, "Negro", genero, "Casual"),
            crearProductoPrueba(2, "Bota Hombre", 150.0f, 43, "Marrón", genero, "Formal")
        );
        
        when(productoRepositorio.findByGenero(genero)).thenReturn(productosMock);

        List<Producto> resultado = controlador.obtenerProductosPorGenero(genero);

        assertEquals(2, resultado.size());
        assertEquals("Zapato Hombre", resultado.get(0).getNombre());
        assertEquals("Bota Hombre", resultado.get(1).getNombre());
        verify(productoRepositorio).findByGenero(genero);
    }

    @Test
    public void testObtenerProductosPorSubcategoria() {
        String genero = "Mujer";
        String tipoZapato = "Sandalia";
        List<Producto> productosMock = Arrays.asList(
            crearProductoPrueba(3, "Sandalia Mujer", 80.0f, 38, "Rojo", genero, tipoZapato)
        );
        
        when(productoRepositorio.findByGeneroAndTipoZapato(genero, tipoZapato)).thenReturn(productosMock);

        List<Producto> resultado = controlador.obtenerProductosPorSubcategoria(genero, tipoZapato);

        assertEquals(1, resultado.size());
        assertEquals("Sandalia Mujer", resultado.get(0).getNombre());
        verify(productoRepositorio).findByGeneroAndTipoZapato(genero, tipoZapato);
    }

    @Test
    public void testObtenerTodosLosProductos() {
        List<Producto> productosMock = Arrays.asList(
            crearProductoPrueba(1, "Zapato A", 100.0f, 42, "Negro", "Hombre", "Casual"),
            crearProductoPrueba(2, "Zapato B", 120.0f, 39, "Rojo", "Mujer", "Formal")
        );
        
        when(productoRepositorio.findAll()).thenReturn(productosMock);

        List<Producto> resultado = controlador.obtenerTodosLosProductos();

        assertEquals(2, resultado.size());
        verify(productoRepositorio).findAll();
    }

    @Test
    public void testObtenerProductoPorId() {
        Integer id = 1;
        Producto productoMock = crearProductoPrueba(id, "Zapato Test", 100.0f, 42, "Negro", "Unisex", "Casual");
        
        when(productoRepositorio.findById(id)).thenReturn(Optional.of(productoMock));
        when(promocionesRepositorio.findByProductoIdProducto(id)).thenReturn(Optional.empty());

        ResponseEntity<?> respuesta = controlador.obtenerProductoPorId(id);

        assertTrue(respuesta.getStatusCode().is2xxSuccessful());
        assertEquals(productoMock, respuesta.getBody());
        verify(productoRepositorio).findById(id);
        verify(promocionesRepositorio).findByProductoIdProducto(id);
    }

    @Test
    public void testObtenerProductoPorIdConPromocion() {
        Integer id = 1;
        Producto productoMock = crearProductoPrueba(id, "Zapato Test", 100.0f, 42, "Negro", "Unisex", "Casual");
        Promociones promocionMock = new Promociones();
        promocionMock.setDescuento(BigDecimal.valueOf(20)); // 20% de descuento
        
        when(productoRepositorio.findById(id)).thenReturn(Optional.of(productoMock));
        when(promocionesRepositorio.findByProductoIdProducto(id)).thenReturn(Optional.of(promocionMock));

        ResponseEntity<?> respuesta = controlador.obtenerProductoPorId(id);

        assertTrue(respuesta.getStatusCode().is2xxSuccessful());
        assertTrue(respuesta.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) respuesta.getBody();
        assertEquals(productoMock, responseBody.get("producto"));
        assertEquals(BigDecimal.valueOf(20), responseBody.get("descuento"));
        
        verify(productoRepositorio).findById(id);
        verify(promocionesRepositorio).findByProductoIdProducto(id);
    }
}