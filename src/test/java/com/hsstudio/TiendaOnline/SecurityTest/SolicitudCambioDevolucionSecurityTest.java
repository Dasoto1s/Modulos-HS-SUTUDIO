package com.hsstudio.TiendaOnline.SecurityTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsstudio.TiendaOnline.Cliente.controlador.SolicitudCambioDevolucionControlador;
import com.hsstudio.TiendaOnline.Cliente.entidad.SolicitudCambioDevolucion;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.SolicitudCambioDevolucionRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import({SecurityTestConfig.class, TestSecurityConfig.class})
public class SolicitudCambioDevolucionSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SolicitudCambioDevolucionRepositorio solicitudRepositorio;

    @MockBean
    private PedidoRepositorio pedidoRepositorio;

    private ObjectMapper objectMapper = new ObjectMapper();

    private SolicitudCambioDevolucion solicitud;

    @BeforeEach
    void setUp() {
        solicitud = new SolicitudCambioDevolucion();
        solicitud.setMotivo_solicitud("Motivo de prueba");
        solicitud.setProducto_relacionado("Producto de prueba");
        solicitud.setMensaje_recivido_cliente("Mensaje de prueba");
        solicitud.setTipo_solicitud("Tipo de prueba");
        solicitud.setNombreCliente("Cliente de prueba");
        solicitud.setNumeroContactoCliente("123456789");
    }
@Test
    void testCrearSolicitudSinAutenticacion() throws Exception {
        mockMvc.perform(post("/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isUnauthorized());
    }

  @Test
    @WithMockUser
    void testCrearSolicitudConAutenticacion() throws Exception {
        when(solicitudRepositorio.save(any(SolicitudCambioDevolucion.class))).thenReturn(solicitud);

        mockMvc.perform(post("/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isCreated());
    }
 @Test
    void testVerSolicitudesSinAutenticacion() throws Exception {
        mockMvc.perform(get("/solicitud"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testVerSolicitudesConAutenticacion() throws Exception {
        mockMvc.perform(get("/solicitud"))
                .andExpect(status().isOk());
    }

    @Test
    void testActualizarEstadoSolicitudSinAutenticacion() throws Exception {
        mockMvc.perform(put("/solicitud/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"Estado_solicitud\":\"1\"}"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser
    void testActualizarEstadoSolicitudConAutenticacion() throws Exception {
        when(solicitudRepositorio.findById(1)).thenReturn(java.util.Optional.of(solicitud));
        when(solicitudRepositorio.save(any(SolicitudCambioDevolucion.class))).thenReturn(solicitud);

        mockMvc.perform(put("/solicitud/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"Estado_solicitud\":\"1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testCrearSolicitudConDatosInvalidos() throws Exception {
        SolicitudCambioDevolucion solicitudInvalida = new SolicitudCambioDevolucion();
        // No establecemos ningún campo, lo que debería resultar en errores de validación

        mockMvc.perform(post("/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitudInvalida)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testActualizarEstadoSolicitudConEstadoInvalido() throws Exception {
        when(solicitudRepositorio.findById(1)).thenReturn(java.util.Optional.of(solicitud));

        mockMvc.perform(put("/solicitud/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"Estado_solicitud\":\"2\"}"))
                .andExpect(status().isBadRequest());
    }
}