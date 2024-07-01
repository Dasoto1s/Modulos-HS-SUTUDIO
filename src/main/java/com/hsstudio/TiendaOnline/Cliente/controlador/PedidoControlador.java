package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.Pedido;
import com.hsstudio.TiendaOnline.Cliente.entidad.Transaccion;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.TransaccionRepositorio;
import com.hsstudio.TiendaOnline.Pago.PaypalConfig;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedidos")
public class PedidoControlador {

    private final PedidoRepositorio pedidoRepositorio;
    private final CarritoComprasRepositorio carritoComprasRepositorio;
    private final TransaccionRepositorio transaccionRepositorio;
    private final APIContext apiContext;
    private final CarritoComprasService carritoComprasService;

    @Autowired
    public PedidoControlador(PedidoRepositorio pedidoRepositorio, CarritoComprasRepositorio carritoComprasRepositorio,
                             TransaccionRepositorio transaccionRepositorio, APIContext apiContext, CarritoComprasService carritoComprasService) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.carritoComprasRepositorio = carritoComprasRepositorio;
        this.transaccionRepositorio = transaccionRepositorio;
        this.apiContext = apiContext;
        this.carritoComprasService = carritoComprasService;
    }

    @PostMapping("/seleccionar-metodo-pago")
    public ResponseEntity<String> seleccionarMetodoPago(@RequestBody Map<String, String> pagoData) {
        Integer idPedido = Integer.parseInt(pagoData.get("idPedido"));
        String metodoPago = pagoData.get("metodoPago");

        Pedido pedido = pedidoRepositorio.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("No se encontró el pedido"));

        if ("paypal".equals(metodoPago)) {
            try {
                return iniciarPago(idPedido, apiContext);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            throw new RuntimeException("Método de pago no soportado");
        }
    }

    @PostMapping("/crear-pedido")
    public ResponseEntity<Integer> crearPedido(@RequestBody Map<String, String> pedidoData,
                                               HttpServletRequest request) {
        String sessionId = request.getHeader("X-Session-Id");

        if (sessionId != null) {
            CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(sessionId);

            if (carrito != null) {
                Pedido pedido = new Pedido();
                pedido.setCarritoCompras(carrito);
                pedido.setDireccionEnvio(pedidoData.get("direccionEnvio"));
                pedido.setFechaPedido(new Date());
                pedido.setDepartamento(pedidoData.get("departamento"));
                pedido.setCiudad(pedidoData.get("ciudad"));

                Pedido pedidoGuardado = pedidoRepositorio.save(pedido);

                return ResponseEntity.ok(pedidoGuardado.getNumeroPedido());
            } else {
                throw new RuntimeException("No se encontró el carrito de compras");
            }
        } else {
            throw new RuntimeException("No se recibió un sessionId válido");
        }
    }

    @GetMapping("/iniciar-pago/{idPedido}")
    public ResponseEntity<String> iniciarPago(@PathVariable Integer idPedido, APIContext apiContext) {
        Pedido pedido = pedidoRepositorio.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("No se encontró el pedido"));

        CarritoCompras carrito = pedido.getCarritoCompras();
        double montoTotal = carrito.getPrecioTotal();

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
                return ResponseEntity.ok(approvalUrl);
            } else {
                throw new RuntimeException("No se pudo obtener la URL de aprobación de PayPal");
            }
        } catch (PayPalRESTException e) {
            throw new RuntimeException("Error al crear el pago con PayPal", e);
        }
    }

    @GetMapping("/pago-exitoso")
    public ResponseEntity<Map<String, String>> pagoExitoso(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = Payment.get(apiContext, paymentId);
            if (payment.getState().equals("approved")) {
                double totalAmount = Double.parseDouble(payment.getTransactions().get(0).getAmount().getTotal());
                String paymentMethod = payment.getPayer().getPaymentMethod();
                String paymentDescription = payment.getTransactions().get(0).getDescription();
                String[] descriptionParts = paymentDescription.split(":");
                Integer idPedido = Integer.parseInt(descriptionParts[1].trim());
                Pedido pedido = pedidoRepositorio.findById(idPedido)
                        .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
                Transaccion transaccion = new Transaccion();
                transaccion.setPedido(pedido);
                transaccion.setMontoTotal(totalAmount);
                transaccion.setMetodoPago(paymentMethod);
                transaccion.setEstado("pagado");
                transaccionRepositorio.save(transaccion);
                String redirectUrl = "http://localhost:3000/confirmacion-pago?idPedido=" + idPedido;
                Map<String, String> response = new HashMap<>();
                response.put("redirectUrl", redirectUrl);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("redirectUrl", "http://localhost:3000/error-pago");
                return ResponseEntity.ok(response);
            }
        } catch (PayPalRESTException e) {
            Map<String, String> response = new HashMap<>();
            response.put("redirectUrl", "http://localhost:3000/error-pago");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/confirmar-pago")
    public ResponseEntity<?> confirmarPago(@RequestParam String idPedido, @RequestHeader("X-Session-Id") String sessionId) {
        carritoComprasService.limpiarCarrito(sessionId);
        String redirectUrl = "http://localhost:3000/informacion-cliente?idPedido=" + idPedido;
        return ResponseEntity.ok(redirectUrl);
    }
}