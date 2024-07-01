package com.hsstudio.TiendaOnline.Cliente.controlador;
import com.hsstudio.TiendaOnline.Admin.dto.PedidoDTO;
import com.hsstudio.TiendaOnline.Cliente.entidad.Pedido;
import com.hsstudio.TiendaOnline.Cliente.entidad.SolicitudCambioDevolucion;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.SolicitudCambioDevolucionRepositorio;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private final PedidoRepositorio pedidoRepositorio;
    private static final Set<String> ESTADOS_PERMITIDOS = new HashSet<>(Arrays.asList("0", "1"));
    private static final String PATRON_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern PATTERN_EMAIL = Pattern.compile(PATRON_EMAIL);

    @Autowired
    public SolicitudCambioDevolucionControlador(SolicitudCambioDevolucionRepositorio solicitudRepositorio, PedidoRepositorio pedidoRepositorio) {
        this.solicitudRepositorio = solicitudRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
    }
@PostMapping
public ResponseEntity<?> crearSolicitud(@RequestBody SolicitudCambioDevolucion solicitud) {
    Map<String, String> errorResponse = new HashMap<>();

    if (solicitud.getMotivo_solicitud() == null || solicitud.getMotivo_solicitud().trim().isEmpty()) {
        errorResponse.put("Motivo_solicitud", "El motivo de la solicitud es obligatorio");
    } else if (solicitud.getMotivo_solicitud().length() > 70) {
        errorResponse.put("Motivo_solicitud", "El motivo de la solicitud no debe exceder los 70 caracteres");
    }

    if (solicitud.getProducto_relacionado() == null || solicitud.getProducto_relacionado().trim().isEmpty()) {
        errorResponse.put("Producto_relacionado", "El producto relacionado es obligatorio");
    } else if (solicitud.getProducto_relacionado().length() > 50) {
        errorResponse.put("Producto_relacionado", "El producto relacionado no debe exceder los 50 caracteres");
    }

    if (solicitud.getMensaje_recivido_cliente() == null || solicitud.getMensaje_recivido_cliente().trim().isEmpty()) {
        errorResponse.put("Mensaje_recivido_cliente", "El mensaje recibido del cliente es obligatorio");
    } else if (solicitud.getMensaje_recivido_cliente().length() > 70) {
        errorResponse.put("Mensaje_recivido_cliente", "El mensaje recibido del cliente no debe exceder los 70 caracteres");
    }

    if (solicitud.getTipo_solicitud() == null || solicitud.getTipo_solicitud().trim().isEmpty()) {
        errorResponse.put("Tipo_solicitud", "El tipo de solicitud es obligatorio");
    } else if (solicitud.getTipo_solicitud().length() > 30) {
        errorResponse.put("Tipo_solicitud", "El tipo de solicitud no debe exceder los 30 caracteres");
    }

    if (solicitud.getNombreCliente() == null || solicitud.getNombreCliente().trim().isEmpty()) {
        errorResponse.put("nombreCliente", "El nombre del cliente es obligatorio");
    } else if (solicitud.getNombreCliente().length() > 40) {
        errorResponse.put("nombreCliente", "El nombre del cliente no debe exceder los 40 caracteres");
    }

    if (solicitud.getNumeroContactoCliente() == null || solicitud.getNumeroContactoCliente().trim().isEmpty()) {
        errorResponse.put("numeroContactoCliente", "El número de contacto del cliente es obligatorio");
    } else if (solicitud.getNumeroContactoCliente().length() > 30) {
        errorResponse.put("numeroContactoCliente", "El número de contacto del cliente no debe exceder los 30 caracteres");
    }

    if (!errorResponse.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Asignar el estado de la solicitud a "0" (pendiente) por defecto
    solicitud.setEstado_solicitud("0");

    solicitudRepositorio.save(solicitud);
    return ResponseEntity.status(HttpStatus.CREATED).body(new HashMap<>());
}

    // Obtener solicitudes de cambio o devolución
    @GetMapping
    public List<SolicitudCambioDevolucion> verSolicitudes() {
        return solicitudRepositorio.findAll();
    }

    // Obtener pedido por su ID
    @GetMapping("/{id}")
public ResponseEntity<SolicitudCambioDevolucion> obtenerSolicitudPorId(@PathVariable Integer id) {
    SolicitudCambioDevolucion solicitud = solicitudRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
    return ResponseEntity.ok(solicitud);
}

   @PutMapping("/{id}")
public ResponseEntity<?> actualizarEstadoSolicitud(@PathVariable Integer id, @RequestBody SolicitudCambioDevolucion solicitudActualizada) {
    SolicitudCambioDevolucion solicitud = solicitudRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
    String nuevoEstado = solicitudActualizada.getEstado_solicitud();
    if (nuevoEstado != null && ESTADOS_PERMITIDOS.contains(nuevoEstado)) {
        solicitud.setEstado_solicitud(nuevoEstado);
        solicitudRepositorio.save(solicitud);
        return ResponseEntity.ok("Estado de la solicitud actualizado con éxito");
    } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El estado de la solicitud no es válido.");
    }
}
}