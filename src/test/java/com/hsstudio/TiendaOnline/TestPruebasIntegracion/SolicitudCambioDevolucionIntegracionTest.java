package com.hsstudio.TiendaOnline.TestPruebasIntegracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsstudio.TiendaOnline.Cliente.entidad.SolicitudCambioDevolucion;
import com.hsstudio.TiendaOnline.Cliente.repositorio.SolicitudCambioDevolucionRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SolicitudCambioDevolucionIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SolicitudCambioDevolucionRepositorio solicitudRepositorio;

    private SolicitudCambioDevolucion solicitudPrueba;

    @BeforeEach
    public void setUp() {
        solicitudRepositorio.deleteAll();

        solicitudPrueba = new SolicitudCambioDevolucion();
        solicitudPrueba.setMotivo_solicitud("Producto defectuoso");
        solicitudPrueba.setProducto_relacionado("Zapatos deportivos");
        solicitudPrueba.setMensaje_recivido_cliente("El producto llegó dañado");
        solicitudPrueba.setTipo_solicitud("Devolución");
        solicitudPrueba.setNombreCliente("Juan Pérez");
        solicitudPrueba.setNumeroContactoCliente("1234567890");
        solicitudPrueba.setCorreo_cliente("juan@example.com");
    }

    @Test
    public void testCrearSolicitud() throws Exception {
        mockMvc.perform(post("/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitudPrueba)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCrearSolicitudInvalida() throws Exception {
        solicitudPrueba.setMotivo_solicitud("");  // Campo obligatorio vacío

        mockMvc.perform(post("/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitudPrueba)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.Motivo_solicitud").value("El motivo de la solicitud es obligatorio"));
    }

    @Test
    public void testObtenerSolicitudes() throws Exception {
        solicitudRepositorio.save(solicitudPrueba);

        mockMvc.perform(get("/solicitud"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].Motivo_solicitud").value("Producto defectuoso"));
    }

    @Test
    public void testObtenerSolicitudPorId() throws Exception {
        SolicitudCambioDevolucion solicitudGuardada = solicitudRepositorio.save(solicitudPrueba);

        mockMvc.perform(get("/solicitud/{id}", solicitudGuardada.getN_solicitud()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Motivo_solicitud").value("Producto defectuoso"));
    }

    @Test
    public void testActualizarEstadoSolicitud() throws Exception {
        SolicitudCambioDevolucion solicitudGuardada = solicitudRepositorio.save(solicitudPrueba);

        SolicitudCambioDevolucion solicitudActualizada = new SolicitudCambioDevolucion();
        solicitudActualizada.setEstado_solicitud("1");

        mockMvc.perform(put("/solicitud/{id}", solicitudGuardada.getN_solicitud())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitudActualizada)))
                .andExpect(status().isOk())
                .andExpect(content().string("Estado de la solicitud actualizado con éxito"));
    }

    @Test
    public void testActualizarEstadoSolicitudInvalido() throws Exception {
        SolicitudCambioDevolucion solicitudGuardada = solicitudRepositorio.save(solicitudPrueba);

        SolicitudCambioDevolucion solicitudActualizada = new SolicitudCambioDevolucion();
        solicitudActualizada.setEstado_solicitud("2");  // Estado inválido

        mockMvc.perform(put("/solicitud/{id}", solicitudGuardada.getN_solicitud())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solicitudActualizada)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El estado de la solicitud no es válido."));
    }
}