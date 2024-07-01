package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.Cliente;
import com.hsstudio.TiendaOnline.Cliente.entidad.Pedido;
import com.hsstudio.TiendaOnline.Cliente.entidad.Transaccion;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.ClienteRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.TransaccionRepositorio;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Calendar;
import java.util.List;

@Service
public class LimpiezaDatosService {
     @Autowired
    private CarritoComprasRepositorio carritoComprasRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private TransaccionRepositorio transaccionRepositorio;

    @Transactional
public void limpiarDatosAntiguos() {
    Date fechaLimite = Date.from(LocalDateTime.now().minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
    List<CarritoCompras> carritosAntiguos = carritoComprasRepositorio.findByFechaCreacionBeforeAndPedidosIsEmpty(fechaLimite);
    
    for (CarritoCompras carrito : carritosAntiguos) {
        String sessionId = carrito.getSessionId();
        
        // Verificar si existe un pedido asociado al carrito
        Pedido pedido = pedidoRepositorio.findByCarritoCompras(carrito);
        if (pedido != null) {
            // Verificar si la transacción asociada al pedido está en estado "completado"
            Transaccion transaccion = transaccionRepositorio.findByPedido(pedido);
            if (transaccion == null || !transaccion.getEstado().equals("completado")) {
                // Si la transacción no existe o no está completada, eliminar el pedido
                pedidoRepositorio.delete(pedido);
            }
        }
        
        // Eliminar cliente asociado si existe
        Cliente cliente = clienteRepositorio.findBySessionId(sessionId);
        if (cliente != null) {
            clienteRepositorio.delete(cliente);
        }
        
        // Eliminar carrito y productos
        carritoComprasRepositorio.deleteCarritoAndProductosById(carrito.getIdCarrito());
    }
}
}