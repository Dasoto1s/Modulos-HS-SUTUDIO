package com.hsstudio.TiendaOnline.Cliente.TestPruebasUnitarias;

import com.hsstudio.TiendaOnline.Cliente.controlador.SolicitudCambioDevolucionControlador;
import com.hsstudio.TiendaOnline.Cliente.entidad.SolicitudCambioDevolucion;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.SolicitudCambioDevolucionRepositorio;
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

public class SolicitudCambioDevolucionControladorTest {

    @InjectMocks
    private SolicitudCambioDevolucionControlador controlador;

    @Mock
    private SolicitudCambioDevolucionRepositorio solicitudRepositorio;

    @Mock
    private PedidoRepositorio pedidoRepositorio;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCrearSolicitud() {
        // Datos de prueba
        SolicitudCambioDevolucion solicitud = new SolicitudCambioDevolucion(
                null,
                "0",
                "Defectuoso",
                "Producto 1",
                "El producto llegó dañado",
                "cliente@example.com",
                "Devolución",
                "Cliente Prueba",
                "1234567890"
        );

        // Validar que se crea la solicitud correctamente
        ResponseEntity<?> response = controlador.crearSolicitud(solicitud);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(solicitudRepositorio, times(1)).save(solicitud);
    }

    @Test
    public void testCrearSolicitudConCamposVacios() {
        // Datos de prueba con campos vacíos
        SolicitudCambioDevolucion solicitud = new SolicitudCambioDevolucion(
                null,
                "0",
                "",
                "",
                "",
                "",
                "",
                "",
                ""
        );

        // Validar que se devuelve un error de campo vacío
        ResponseEntity<?> response = controlador.crearSolicitud(solicitud);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testVerSolicitudes() {
        // Datos de prueba
        List<SolicitudCambioDevolucion> solicitudes = new ArrayList<>();
        solicitudes.add(new SolicitudCambioDevolucion(1, "0", "Defectuoso", "Producto 1", "El producto llegó dañado", "cliente@example.com", "Devolución", "Cliente Prueba", "1234567890"));
        solicitudes.add(new SolicitudCambioDevolucion(2, "1", "Cambio de talla", "Producto 2", "Necesito cambiar la talla", "cliente2@example.com", "Cambio", "Cliente Prueba 2", "0987654321"));

        // Configurar el comportamiento del repositorio
        when(solicitudRepositorio.findAll()).thenReturn(solicitudes);

        // Validar que se obtienen las solicitudes correctamente
        List<SolicitudCambioDevolucion> result = controlador.verSolicitudes();
        assertEquals(2, result.size());
        assertEquals("Defectuoso", result.get(0).getMotivo_solicitud());
        assertEquals("Cambio de talla", result.get(1).getMotivo_solicitud());
    }

    @Test
    public void testObtenerSolicitudPorId() {
        // Datos de prueba
        SolicitudCambioDevolucion solicitud = new SolicitudCambioDevolucion(1, "0", "Defectuoso", "Producto 1", "El producto llegó dañado", "cliente@example.com", "Devolución", "Cliente Prueba", "1234567890");

        // Configurar el comportamiento del repositorio
        when(solicitudRepositorio.findById(1)).thenReturn(Optional.of(solicitud));

        // Validar que se obtiene la solicitud correctamente
        ResponseEntity<SolicitudCambioDevolucion> response = controlador.obtenerSolicitudPorId(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(solicitud, response.getBody());
    }

    @Test
    public void testActualizarEstadoSolicitud() {
        // Datos de prueba
        SolicitudCambioDevolucion solicitud = new SolicitudCambioDevolucion(1, "0", "Defectuoso", "Producto 1", "El producto llegó dañado", "cliente@example.com", "Devolución", "Cliente Prueba", "1234567890");
        SolicitudCambioDevolucion solicitudActualizada = new SolicitudCambioDevolucion(1, "1", "Defectuoso", "Producto 1", "El producto llegó dañado", "cliente@example.com", "Devolución", "Cliente Prueba", "1234567890");

        // Configurar el comportamiento del repositorio
        when(solicitudRepositorio.findById(1)).thenReturn(Optional.of(solicitud));
        when(solicitudRepositorio.save(any(SolicitudCambioDevolucion.class))).thenReturn(solicitudActualizada);

        // Validar que se actualiza el estado de la solicitud correctamente
        ResponseEntity<?> response = controlador.actualizarEstadoSolicitud(1, solicitudActualizada);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Estado de la solicitud actualizado con éxito", response.getBody());
        verify(solicitudRepositorio, times(1)).save(solicitud);
    }

    @Test
    public void testActualizarEstadoSolicitudConEstadoInvalido() {
        // Datos de prueba
        SolicitudCambioDevolucion solicitud = new SolicitudCambioDevolucion(1, "0", "Defectuoso", "Producto 1", "El producto llegó dañado", "cliente@example.com", "Devolución", "Cliente Prueba", "1234567890");
        SolicitudCambioDevolucion solicitudActualizada = new SolicitudCambioDevolucion(1, "2", "Defectuoso", "Producto 1", "El producto llegó dañado", "cliente@example.com", "Devolución", "Cliente Prueba", "1234567890");

        // Configurar el comportamiento del repositorio
        when(solicitudRepositorio.findById(1)).thenReturn(Optional.of(solicitud));

        // Validar que se devuelve un error de estado inválido
        ResponseEntity<?> response = controlador.actualizarEstadoSolicitud(1, solicitudActualizada);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El estado de la solicitud no es válido.", response.getBody());
        verify(solicitudRepositorio, never()).save(any(SolicitudCambioDevolucion.class));
    }
}