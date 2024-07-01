package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Cliente.entidad.Cliente;
import com.hsstudio.TiendaOnline.Cliente.repositorio.ClienteRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClienteControladorTest {

    @InjectMocks
    private ClienteControlador clienteControlador;

    @Mock
    private ClienteRepositorio clienteRepositorio;

    @Mock
    private CarritoComprasRepositorio carritoComprasRepositorio;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

   @Test
public void testObtenerTodosLosClientes() {
    // Datos de prueba
    List<Cliente> clientes = new ArrayList<>();
    clientes.add(new Cliente("1", "Cliente 1", "cliente1@example.com", new BigInteger("1234567890")));
    clientes.add(new Cliente("2", "Cliente 2", "cliente2@example.com", new BigInteger("0987654321")));

    // Configurar el comportamiento del repositorio
    when(clienteRepositorio.findAll()).thenReturn(clientes);

    // Llamar al método del controlador
    ResponseEntity<List<Cliente>> response = clienteControlador.obtenerTodosLosClientes();

    // Verificar que se obtienen los clientes correctamente
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
    assertEquals("Cliente 1", response.getBody().get(0).getNombre());
    assertEquals("Cliente 2", response.getBody().get(1).getNombre());
}

    @Test
    public void testObtenerClientePorId() {
        // Datos de prueba
        Cliente cliente = new Cliente("1", "Cliente 1", "cliente1@example.com", new BigInteger("1234567890"));

        // Configurar el comportamiento del repositorio
        when(clienteRepositorio.findById("1")).thenReturn(Optional.of(cliente));

        // Llamar al método del controlador
        ResponseEntity<Cliente> response = clienteControlador.obtenerClientePorId("1");

        // Verificar que se obtiene el cliente correctamente
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cliente, response.getBody());
    }

    @Test
    public void testCrearCliente() {
        // Datos de prueba
        Cliente nuevoCliente = new Cliente("123", "Nuevo Cliente", "nuevo@example.com", new BigInteger("9876543210"));
        String sessionId = "123";

        // Configurar el comportamiento del repositorio
        when(clienteRepositorio.save(nuevoCliente)).thenReturn(nuevoCliente);
        when(clienteRepositorio.findById(sessionId)).thenReturn(Optional.of(nuevoCliente));

        // Llamar al método del controlador
        ResponseEntity<?> response = clienteControlador.crearCliente(nuevoCliente, sessionId);

        // Verificar que se crea el cliente correctamente
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(nuevoCliente, response.getBody());
    }

    @Test
    public void testCrearClienteSinSessionId() {
        // Datos de prueba
        Cliente nuevoCliente = new Cliente("123", "Nuevo Cliente", "nuevo@example.com", new BigInteger("9876543210"));
        String sessionId = null;

        // Llamar al método del controlador
        ResponseEntity<?> response = clienteControlador.crearCliente(nuevoCliente, sessionId);

        // Verificar que se devuelve un error por falta de sessionId
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No se recibió un sessionId válido", response.getBody());
    }
}