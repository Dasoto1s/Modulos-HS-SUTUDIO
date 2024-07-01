package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Cliente.entidad.Destacados;
import com.hsstudio.TiendaOnline.Cliente.repositorio.DestacadosRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DestacadosControladorTest {

    @InjectMocks
    private DestacadosControlador destacadosControlador;

    @Mock
    private DestacadosRepositorio destacadosRepositorio;

    @Mock
    private ProductoRepositorio productoRepositorio;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testObtenerProductosDestacados() {
        // Crear algunos productos destacados de prueba
        Destacados destacado1 = new Destacados(1, new Producto(1, "Producto 1", "Descripción 1", 10.0f, 42, "Negro", "Hombre", new byte[0], "Casual", null));
        Destacados destacado2 = new Destacados(2, new Producto(2, "Producto 2", "Descripción 2", 15.0f, 39, "Rojo", "Mujer", new byte[0], "Formal", null));
        List<Destacados> destacados = new ArrayList<>();
        destacados.add(destacado1);
        destacados.add(destacado2);

        // Configurar el comportamiento del repositorio
        when(destacadosRepositorio.findAll()).thenReturn(destacados);

        // Llamar al método del controlador
        List<Destacados> result = destacadosControlador.obtenerProductosDestacados();

        // Verificar que el resultado es el esperado
        assertEquals(2, result.size());
        assertEquals("Producto 1", result.get(0).getProducto().getNombre());
        assertEquals("Producto 2", result.get(1).getProducto().getNombre());
    }

    @Test
    public void testAgregarProductoDestacado() {
        // Crear un producto de prueba
        Producto producto = new Producto(1, "Producto 1", "Descripción 1", 10.0f, 42, "Negro", "Hombre", new byte[0], "Casual", null);

        // Configurar el comportamiento del repositorio
        when(destacadosRepositorio.count()).thenReturn(11L);
        when(productoRepositorio.findById(1)).thenReturn(Optional.of(producto));
        when(destacadosRepositorio.save(any(Destacados.class))).thenReturn(new Destacados(1, producto));

        // Llamar al método del controlador
        ResponseEntity<String> result = destacadosControlador.agregarProductoDestacado(1);

        // Verificar que la respuesta es la esperada
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Producto agregado a destacados", result.getBody());
    }

    @Test
    public void testEliminarProductoDestacado() {
        // Crear un producto destacado de prueba
        Producto producto = new Producto(1, "Producto 1", "Descripción 1", 10.0f, 42, "Negro", "Hombre", new byte[0], "Casual", null);
        Destacados destacado = new Destacados(1, producto);

        // Configurar el comportamiento del repositorio
        when(destacadosRepositorio.findById(1)).thenReturn(Optional.of(destacado));
        doNothing().when(destacadosRepositorio).delete(destacado);

        // Llamar al método del controlador
        ResponseEntity<String> result = destacadosControlador.eliminarProductoDestacado(1);

        // Verificar que la respuesta es la esperada
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Producto eliminado de destacados", result.getBody());
    }
}