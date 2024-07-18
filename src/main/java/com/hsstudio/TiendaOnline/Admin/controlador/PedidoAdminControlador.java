package com.hsstudio.TiendaOnline.Admin.controlador;

import com.hsstudio.TiendaOnline.Admin.dto.PedidoDTO;
import com.hsstudio.TiendaOnline.Cliente.entidad.Pedido;
import com.hsstudio.TiendaOnline.Cliente.entidad.Cliente;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.repositorio.ClienteRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import java.util.Arrays;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/admin/pedidos")
public class PedidoAdminControlador {
    private static final Logger logger = LoggerFactory.getLogger(PedidoAdminControlador.class);
    private final PedidoRepositorio pedidoRepositorio;
    private final ClienteRepositorio clienteRepositorio;
    private static final Set<String> ESTADOS_PERMITIDOS = new HashSet<>(Arrays.asList("pendiente", "atendido"));

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
public ResponseEntity<PedidoDTO> obtenerPedidoPorId(@PathVariable Integer id) {
    Pedido pedido = pedidoRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    return ResponseEntity.ok(convertirAPedidoDTO(pedido));
}

    private PedidoDTO convertirAPedidoDTO(Pedido pedido) {
        PedidoDTO pedidoDTO = new PedidoDTO();
        pedidoDTO.setNumeroPedido(pedido.getNumeroPedido());
        pedidoDTO.setDireccionEnvio(pedido.getDireccionEnvio());
        pedidoDTO.setFechaPedido(pedido.getFechaPedido());
        pedidoDTO.setDepartamento(pedido.getDepartamento());
        pedidoDTO.setCiudad(pedido.getCiudad());
        pedidoDTO.setEstadoPedido(pedido.getEstadoPedido());
 

        CarritoCompras carritoCompras = pedido.getCarritoCompras();
        if (carritoCompras != null) {
            pedidoDTO.setNumeroProductos(carritoCompras.getNumeroProductos());
            pedidoDTO.setPrecioTotal(carritoCompras.getPrecioTotal());
            pedidoDTO.setProductos(carritoCompras.getProductos());

            String sessionId = carritoCompras.getSessionId();
            Cliente cliente = clienteRepositorio.findBySessionId(sessionId);
            if (cliente != null) {
                pedidoDTO.setNombreCliente(cliente.getNombre());
                pedidoDTO.setCorreoCliente(cliente.getCorreo());
                pedidoDTO.setTelefonoCliente(cliente.getTelefono());
            }
        }

        return pedidoDTO;
    }
@PutMapping("/{id}/estado")
public ResponseEntity<?> actualizarEstadoPedido(@PathVariable Integer id, @RequestBody Pedido pedidoActualizado) {
    Pedido pedido = pedidoRepositorio.findById(id)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    String nuevoEstado = pedidoActualizado.getEstadoPedido();
    if (nuevoEstado != null && ESTADOS_PERMITIDOS.contains(nuevoEstado)) {
        pedido.setEstadoPedido(nuevoEstado);
        pedidoRepositorio.save(pedido);
        return ResponseEntity.ok("Estado del pedido actualizado con éxito");
    } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El estado del pedido no es válido.");
    }
}

}