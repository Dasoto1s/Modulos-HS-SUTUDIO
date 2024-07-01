package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.entidad.ProductoDTO;
import com.hsstudio.TiendaOnline.Admin.entidad.Inventario;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.entidad.Destacados;
import com.hsstudio.TiendaOnline.Cliente.entidad.Promociones;
import com.hsstudio.TiendaOnline.Cliente.repositorio.DestacadosRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        MockitoAnnotations.openMocks(this);
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

        ResponseEntity<Producto> respuesta = controlador.obtenerProductoPorId(id);

        assertTrue(respuesta.getStatusCode().is2xxSuccessful());
        assertEquals("Zapato Test", respuesta.getBody().getNombre());
        verify(productoRepositorio).findById(id);
    }

    
}