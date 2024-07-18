package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.Pedido;
import com.hsstudio.TiendaOnline.Cliente.entidad.Promociones;
import com.hsstudio.TiendaOnline.Cliente.entidad.Transaccion;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.TransaccionRepositorio;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/pedidos")
public class PedidoControlador {

   

    private static final Logger logger = LoggerFactory.getLogger(PedidoControlador.class);

    private final PedidoRepositorio pedidoRepositorio;
    private final CarritoComprasRepositorio carritoComprasRepositorio;
    private final TransaccionRepositorio transaccionRepositorio;
    private final APIContext apiContext;
    private final CarritoComprasService carritoComprasService;
    private final PromocionesRepositorio promocionesRepositorio;

    @Autowired
    public PedidoControlador(PedidoRepositorio pedidoRepositorio, CarritoComprasRepositorio carritoComprasRepositorio,
                             TransaccionRepositorio transaccionRepositorio, APIContext apiContext, 
                             CarritoComprasService carritoComprasService, PromocionesRepositorio promocionesRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.carritoComprasRepositorio = carritoComprasRepositorio;
        this.transaccionRepositorio = transaccionRepositorio;
        this.apiContext = apiContext;
        this.carritoComprasService = carritoComprasService;
        this.promocionesRepositorio = promocionesRepositorio;
    }

    @PostMapping("/seleccionar-metodo-pago")
    public ResponseEntity<String> seleccionarMetodoPago(@RequestBody Map<String, String> pagoData) {
        logger.info("Iniciando selección de método de pago: {}", pagoData);
        Integer idPedido = Integer.parseInt(pagoData.get("idPedido"));
        String metodoPago = pagoData.get("metodoPago");

        Pedido pedido = pedidoRepositorio.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("No se encontró el pedido"));

        if ("paypal".equals(metodoPago)) {
            try {
                return iniciarPago(idPedido, apiContext);
            } catch (RuntimeException e) {
                logger.error("Error al iniciar el pago: ", e);
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            logger.error("Método de pago no soportado: {}", metodoPago);
            throw new RuntimeException("Método de pago no soportado");
        }
    }
  @PostMapping("/crear-pedido")
    @Transactional
    public ResponseEntity<?> crearPedido(@RequestBody Map<String, Object> pedidoData,
                                         HttpServletRequest request) {
        logger.info("Iniciando creación de pedido: {}", pedidoData);
        String sessionId = request.getHeader("X-Session-Id");
        
        if (sessionId == null) {
            logger.error("No se recibió un sessionId válido");
            return ResponseEntity.badRequest().body("No se recibió un sessionId válido");
        }


        CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(sessionId);
        
        if (carrito == null) {
            logger.error("No se encontró el carrito de compras para el sessionId: {}", sessionId);
            return ResponseEntity.badRequest().body("No se encontró el carrito de compras");
        }

        Pedido pedido = new Pedido();
        pedido.setCarritoCompras(carrito);
        pedido.setDireccionEnvio((String) pedidoData.get("direccionEnvio"));
        pedido.setFechaPedido(new Date());
        pedido.setDepartamento((String) pedidoData.get("departamento"));
        pedido.setCiudad((String) pedidoData.get("ciudad"));
        
        double totalSinDescuento = carrito.getPrecioTotal();
        double totalConDescuento = 0;
        
        for (Producto producto : carrito.getProductos()) {
            Optional<Promociones> promocionOptional = promocionesRepositorio.findByProductoIdProducto(producto.getIdProducto());
            if (promocionOptional.isPresent()) {
                Promociones promocion = promocionOptional.get();
                double porcentajeDescuento = promocion.getDescuento().doubleValue() / 100;
                double precioConDescuento = producto.getPrecio() - (producto.getPrecio() * porcentajeDescuento);
                totalConDescuento += precioConDescuento;
            } else {
                totalConDescuento += producto.getPrecio();
            }
        }
        
        pedido.setPrecioTotal(totalSinDescuento);
        pedido.setTotalConDescuento(totalConDescuento);
                
        Pedido pedidoGuardado = pedidoRepositorio.save(pedido);
        if (pedidoGuardado == null) {
            logger.error("No se pudo guardar el pedido");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el pedido");
        }
        logger.info("Pedido creado exitosamente: {}", pedidoGuardado);
        
        return ResponseEntity.ok(pedidoGuardado.getNumeroPedido());
    }
    @GetMapping("/iniciar-pago/{idPedido}")
public ResponseEntity<String> iniciarPago(@PathVariable Integer idPedido, APIContext apiContext) {
    logger.info("Iniciando pago para el pedido: {}", idPedido);
    Pedido pedido = pedidoRepositorio.findById(idPedido)
            .orElseThrow(() -> new RuntimeException("No se encontró el pedido"));

    double montoTotal;
    if (pedido.getTotalConDescuento() != null && pedido.getTotalConDescuento() > 0) {
        montoTotal = pedido.getTotalConDescuento(); // Utilizar el total con descuento si está disponible
    } else {
        montoTotal = pedido.getPrecioTotal(); // Utilizar el total sin descuento si no hay descuentos aplicados
    }

    Payment payment = new Payment();
    payment.setIntent("sale");
    payment.setPayer(new Payer().setPaymentMethod("paypal"));

    Transaction transaction = new Transaction();
    transaction.setAmount(new Amount().setCurrency("USD").setTotal(String.valueOf(montoTotal)));
    transaction.setDescription("Pago de compra:" + idPedido);
    payment.setTransactions(List.of(transaction));

    RedirectUrls redirectUrls = new RedirectUrls();
    redirectUrls.setCancelUrl("http://localhost:8080/pagos/pago-cancelado");
    redirectUrls.setReturnUrl("http://localhost:8080/pagos/pago-exitoso");
    payment.setRedirectUrls(redirectUrls);

    try {
        Payment createdPayment = payment.create(apiContext);
        String approvalUrl = createdPayment.getLinks().stream()
                .filter(link -> link.getRel().equals("approval_url"))
                .findFirst()
                .map(Links::getHref)
                .orElse(null);

        if (approvalUrl != null) {
            logger.info("URL de aprobación de PayPal obtenida: {}", approvalUrl);
            return ResponseEntity.ok(approvalUrl);
        } else {
            logger.error("No se pudo obtener la URL de aprobación de PayPal");
            throw new RuntimeException("No se pudo obtener la URL de aprobación de PayPal");
        }
    } catch (PayPalRESTException e) {
        logger.error("Error al crear el pago con PayPal", e);
        throw new RuntimeException("Error al crear el pago con PayPal", e);
    }
}


    @PostMapping("/confirmar-pago")
    public ResponseEntity<?> confirmarPago(@RequestParam String idPedido, @RequestHeader("X-Session-Id") String sessionId) {
        logger.info("Confirmando pago para el pedido: {} con sessionId: {}", idPedido, sessionId);
        carritoComprasService.limpiarCarrito(sessionId);
        String redirectUrl = "http://localhost:3000/informacion-cliente?idPedido=" + idPedido;
        logger.info("Redirigiendo a: {}", redirectUrl);
        return ResponseEntity.ok(redirectUrl);
    }

 @GetMapping("/estado-pago/{idPedido}")
public ResponseEntity<?> obtenerEstadoPago(@PathVariable Integer idPedido) {
    logger.info("Obteniendo estado de pago para el pedido: {}", idPedido);
    try {
        Pedido pedido = pedidoRepositorio.findById(idPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado"));

        Map<String, Object> respuesta = new HashMap<>();
        Map<String, Object> infoPedido = new HashMap<>();

        infoPedido.put("numeroPedido", pedido.getNumeroPedido());
        infoPedido.put("direccionEnvio", pedido.getDireccionEnvio());

        Optional<Transaccion> transaccionOpt = Optional.ofNullable(transaccionRepositorio.findByPedido(pedido));

        if (transaccionOpt.isPresent()) {
            Transaccion transaccion = transaccionOpt.get();
            boolean pagado = "pagado".equals(transaccion.getEstado());
            respuesta.put("pagado", pagado);
            respuesta.put("estado", transaccion.getEstado());
            infoPedido.put("total", transaccion.getMontoTotal());
        } else {
            logger.warn("Transacción no encontrada para el pedido: {}", idPedido);
            respuesta.put("pagado", false);
            respuesta.put("estado", "pendiente");
            infoPedido.put("total", pedido.getPrecioTotal());
        }

        List<Map<String, Object>> productos = pedido.getCarritoCompras().getProductos().stream()
                .map(producto -> {
                    Map<String, Object> infoProducto = new HashMap<>();
                    infoProducto.put("nombre", producto.getNombre());
                    infoProducto.put("precio", producto.getPrecio());
                    return infoProducto;
                })
                .collect(Collectors.toList());

        infoPedido.put("productos", productos);
        respuesta.put("infoPedido", infoPedido);

        logger.info("Información del pedido preparada: {}", infoPedido);
        return ResponseEntity.ok(respuesta);
    } catch (EntityNotFoundException e) {
        logger.error("Pedido no encontrado: {}", idPedido);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
        logger.error("Error al obtener el estado del pago para el pedido {}: {}", idPedido, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
    }
}
}