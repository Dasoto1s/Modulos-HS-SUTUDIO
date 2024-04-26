package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Cliente.entidad.SolicitudCambioDevolucion;
import com.hsstudio.TiendaOnline.Cliente.repositorio.SolicitudCambioDevolucionRepositorio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solicitud")
public class SolicitudCambioDevolucionControlador {
    private final SolicitudCambioDevolucionRepositorio solicitudRepositorio;

    @Autowired
    public SolicitudCambioDevolucionControlador(SolicitudCambioDevolucionRepositorio solicitudRepositorio) {
        this.solicitudRepositorio = solicitudRepositorio;
    }

    // Crear una nueva solicitud
    @PostMapping
    public ResponseEntity<?> crearSolicitud(@RequestBody SolicitudCambioDevolucion solicitud) {
        solicitudRepositorio.save(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud creada con éxito");
    }

    // Ver todas las solicitudes
    @GetMapping
    public List<SolicitudCambioDevolucion> verSolicitudes() {
        return solicitudRepositorio.findAll();
    }
}
