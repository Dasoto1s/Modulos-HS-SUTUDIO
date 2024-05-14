package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Cliente.entidad.Cliente;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.repositorio.ClienteRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cliente")
public class ClienteControlador {

    private final ClienteRepositorio clienteRepositorio;
    private final CarritoComprasRepositorio carritoComprasRepositorio;

    @Autowired
    public ClienteControlador(ClienteRepositorio clienteRepositorio, CarritoComprasRepositorio carritoComprasRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
        this.carritoComprasRepositorio = carritoComprasRepositorio;
    }

    // Obtener todos los clientes
    @GetMapping
    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepositorio.findAll();
    }

    // Obtener un cliente por su ID
    @GetMapping("/{id}")
    public Cliente obtenerClientePorId(@PathVariable String id) {
        Optional<Cliente> cliente = clienteRepositorio.findById(id);
        return cliente.orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    @PostMapping
public Cliente crearCliente(@RequestBody Cliente nuevoCliente, HttpServletRequest request, HttpServletResponse response) {
    // Obtener el valor de "carritoId" de la sesión
    HttpSession session = request.getSession();
    String carritoId = (String) session.getAttribute("carritoId");

    if (carritoId != null) {
        // Asignar el valor de "carritoId" al campo "idCliente" del nuevo cliente
        nuevoCliente.setIdCliente(carritoId);

        // Guardar el nuevo cliente en la base de datos
        Cliente clienteGuardado = clienteRepositorio.save(nuevoCliente);

        return clienteGuardado;
    }

    // Si no se encontró un valor para "carritoId" en la sesión, lanzar una excepción o manejar el caso según tus necesidades
    throw new RuntimeException("No se encontró un valor para 'carritoId' en la sesión");
}
}