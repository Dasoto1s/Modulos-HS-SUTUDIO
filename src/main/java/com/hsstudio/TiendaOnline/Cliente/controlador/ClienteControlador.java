package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Cliente.entidad.Cliente;
import com.hsstudio.TiendaOnline.Cliente.repositorio.ClienteRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/cliente")
public class ClienteControlador {
    private final ClienteRepositorio clienteRepositorio;
    private final CarritoComprasRepositorio carritoComprasRepositorio;


    @Autowired
    public ClienteControlador(ClienteRepositorio clienteRepositorio, 
                              CarritoComprasRepositorio carritoComprasRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
        this.carritoComprasRepositorio = carritoComprasRepositorio;
  
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodosLosClientes() {
        List<Cliente> clientes = clienteRepositorio.findAll();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable String id) {
        Optional<Cliente> cliente = clienteRepositorio.findById(id);
        return cliente.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> crearCliente(@RequestBody Cliente nuevoCliente, @RequestHeader("X-Session-Id") String sessionId) {
        System.out.println("Iniciando creación de cliente");
        System.out.println("Session ID recibido en crearCliente: " + sessionId);
        System.out.println("Datos del cliente recibidos: " + nuevoCliente);

        if (sessionId == null || sessionId.isEmpty()) {
            return ResponseEntity.badRequest().body("No se recibió un sessionId válido");
        }

        nuevoCliente.setIdCliente(sessionId);

        try {
            System.out.println("Intentando guardar el cliente en la base de datos...");
            Cliente clienteGuardado = clienteRepositorio.save(nuevoCliente);
            System.out.println("Cliente guardado exitosamente: " + clienteGuardado);

            Cliente clienteVerificado = clienteRepositorio.findById(sessionId).orElse(null);
            if (clienteVerificado != null) {
                System.out.println("Cliente verificado en la base de datos: " + clienteVerificado);
                return ResponseEntity.ok(clienteVerificado);
            } else {
                System.out.println("¡ADVERTENCIA! No se pudo verificar el cliente en la base de datos después de guardarlo.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("El cliente se guardó pero no se pudo verificar en la base de datos");
            }
        } catch (Exception e) {
            System.err.println("Error al guardar el cliente: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el cliente: " + e.getMessage());
        }
    }
    
    
   
}