package com.hsstudio.TiendaOnline.Admin.controlador;

import com.hsstudio.TiendaOnline.Admin.dto.PedidoDTO;
import com.hsstudio.TiendaOnline.Cliente.entidad.Pedido;
import com.hsstudio.TiendaOnline.Cliente.entidad.Cliente;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.repositorio.ClienteRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/admin/pedidos")
public class PedidoAdminControlador {
    private final PedidoRepositorio pedidoRepositorio;
    private final ClienteRepositorio clienteRepositorio;

    @Autowired
    public PedidoAdminControlador(PedidoRepositorio pedidoRepositorio, ClienteRepositorio clienteRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.clienteRepositorio = clienteRepositorio;
    }

    @GetMapping
    public List<PedidoDTO> obtenerTodosLosPedidos() {
        List<Pedido> pedidos = pedidoRepositorio.findAll();
        return pedidos.stream().map(this::convertirAPedidoDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PedidoDTO obtenerPedidoPorId(@PathVariable Integer id) {
        Pedido pedido = pedidoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        return convertirAPedidoDTO(pedido);
    }

    private PedidoDTO convertirAPedidoDTO(Pedido pedido) {
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNumeroPedido(pedido.getNumeroPedido());
        pedidoDTO.setDireccionEnvio(pedido.getDireccionEnvio());
        pedidoDTO.setFechaPedido(pedido.getFechaPedido());
        pedidoDTO.setDepartamento(pedido.getDepartamento());
        pedidoDTO.setCiudad(pedido.getCiudad());
        pedidoDTO.setEstado_solicitud(pedido.getEstado_solicitud());

        CarritoCompras carritoCompras = pedido.getCarritoCompras();
        pedidoDTO.setNumeroProductos(carritoCompras.getNumeroProductos());
        pedidoDTO.setPrecioTotal(carritoCompras.getPrecioTotal());
        pedidoDTO.setProductos(carritoCompras.getProductos());

        // Obtener la información del cliente utilizando el session_id
        String sessionId = carritoCompras.getSessionId();
        Cliente cliente = clienteRepositorio.findBySessionId(sessionId);
        if (cliente != null) {
            pedidoDTO.setNombreCliente(cliente.getNombre());
            pedidoDTO.setCorreoCliente(cliente.getCorreo());
            pedidoDTO.setTelefonoCliente(cliente.getTelefono());
            pedidoDTO.setMetodoPagoCliente(cliente.getMetodoPago());
        }

        return pedidoDTO;
    }
@PutMapping("/{id}")
public ResponseEntity<?> actualizarEstadoPedido(@PathVariable Integer id, @RequestBody String nuevoEstado) {
    Pedido pedido = pedidoRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    if (nuevoEstado != null && (nuevoEstado.equals("0") || nuevoEstado.equals("1"))) {
        pedido.setEstado_solicitud(nuevoEstado);
        pedidoRepositorio.save(pedido);
        return ResponseEntity.ok("Estado del pedido actualizado con éxito");
    } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El estado del pedido no es válido.");
    }
}
}