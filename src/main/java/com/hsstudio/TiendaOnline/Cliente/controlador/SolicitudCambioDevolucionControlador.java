package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Cliente.entidad.SolicitudCambioDevolucion;
import com.hsstudio.TiendaOnline.Cliente.repositorio.SolicitudCambioDevolucionRepositorio;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solicitud")
public class SolicitudCambioDevolucionControlador {

    private final SolicitudCambioDevolucionRepositorio solicitudRepositorio;

    private static final Set<String> ESTADOS_PERMITIDOS = new HashSet<>(Arrays.asList("0", "1"));
    private static final String PATRON_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern PATTERN_EMAIL = Pattern.compile(PATRON_EMAIL);

    @Autowired
    public SolicitudCambioDevolucionControlador(SolicitudCambioDevolucionRepositorio solicitudRepositorio) {
        this.solicitudRepositorio = solicitudRepositorio;
    }

  @PostMapping
public ResponseEntity<?> crearSolicitud(@RequestBody SolicitudCambioDevolucion solicitud) {
    if (solicitud.getEstado_solicitud() == null ||
        !ESTADOS_PERMITIDOS.contains(solicitud.getEstado_solicitud()) ||
        solicitud.getMotivo_solicitud() == null ||
        solicitud.getProducto_relacionado() == null ||
        solicitud.getMensaje_recivido_cliente() == null ||
        solicitud.getTipo_solicitud() == null ||
        solicitud.getNombreCliente() == null ||
        solicitud.getNumeroContactoCliente() == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Todos los campos obligatorios deben ser ingresados y el estado de la solicitud debe ser válido.");
    } else if (solicitud.getCorreo_cliente() != null && !PATTERN_EMAIL.matcher(solicitud.getCorreo_cliente()).matches()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El correo electrónico ingresado no tiene un formato válido.");
    }

    solicitudRepositorio.save(solicitud);
    return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud creada con éxito");
}
    // Obtener solicitudes de cambio o devolucion
    @GetMapping
    public List<SolicitudCambioDevolucion> verSolicitudes() {
        return solicitudRepositorio.findAll();
    }
    
    // Obtener solicitud  por su ID
    
    @GetMapping("/{id}")
public ResponseEntity<SolicitudCambioDevolucion> obtenerSolicitudPorId(@PathVariable Integer id) {
    SolicitudCambioDevolucion solicitud = solicitudRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
    return ResponseEntity.ok(solicitud);
}
    //Actualizar el estado de solicitud

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEstadoSolicitud(@PathVariable Integer id, @RequestBody SolicitudCambioDevolucion solicitudActualizada) {
        SolicitudCambioDevolucion solicitud = solicitudRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitudActualizada.getEstado_solicitud() != null &&
                ESTADOS_PERMITIDOS.contains(solicitudActualizada.getEstado_solicitud())) {
            solicitud.setEstado_solicitud(solicitudActualizada.getEstado_solicitud());
            solicitudRepositorio.save(solicitud);
            return ResponseEntity.ok("Estado de solicitud actualizado con éxito");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El estado de la solicitud no es válido.");
        }
}
}