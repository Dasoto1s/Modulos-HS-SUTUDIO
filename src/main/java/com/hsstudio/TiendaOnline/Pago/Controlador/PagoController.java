package com.hsstudio.TiendaOnline.Pago.Controlador;

import com.hsstudio.TiendaOnline.Pago.PaypalConfig;
import com.hsstudio.TiendaOnline.Cliente.entidad.Pedido;
import com.hsstudio.TiendaOnline.Cliente.entidad.Transaccion;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.TransaccionRepositorio;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/pagos")
public class PagoController {
    private final PaypalConfig paypalConfig;
    private final PedidoRepositorio pedidoRepositorio;
    private final TransaccionRepositorio transaccionRepositorio;

    @Autowired
    public PagoController(PaypalConfig paypalConfig, PedidoRepositorio pedidoRepositorio, TransaccionRepositorio transaccionRepositorio) {
        this.paypalConfig = paypalConfig;
        this.pedidoRepositorio = pedidoRepositorio;
        this.transaccionRepositorio = transaccionRepositorio;
    }

    @PostMapping("/procesar-pago")
    public String procesarPago(@RequestParam("monto") double monto, @RequestParam("idPedido") Integer idPedido) throws PayPalRESTException {
        APIContext apiContext = new APIContext(
                paypalConfig.getClientId(),
                paypalConfig.getClientSecret(),
                paypalConfig.getMode()
        );
        // Crear el objeto Payment
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(new Payer().setPaymentMethod("paypal"));
        // Configurar la transacción
        Transaction transaction = new Transaction();
        transaction.setAmount(new Amount().setCurrency("USD").setTotal(String.format("%.2f", monto)));
        transaction.setDescription("Pago de pedido:" + idPedido);
        payment.setTransactions(List.of(transaction));
        // Configurar las URL de retorno y cancelación
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:3000/pago-cancelado");
        redirectUrls.setReturnUrl("http://localhost:8080/pagos/pago-exitoso");
        payment.setRedirectUrls(redirectUrls);
        // Crear el pago
        Payment createdPayment = payment.create(apiContext);
        // Obtener la URL de aprobación del pago
        String approvalUrl = createdPayment.getLinks().stream()
                .filter(link -> link.getRel().equals("approval_url"))
                .findFirst()
                .map(Links::getHref)
                .orElse(null);
        // Redirigir al usuario a la URL de aprobación
        return approvalUrl;
    }

    @GetMapping("/pago-exitoso")
    public String pagoExitoso(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) throws PayPalRESTException {
        APIContext apiContext = new APIContext(
                paypalConfig.getClientId(),
                paypalConfig.getClientSecret(),
                paypalConfig.getMode()
        );
        // Obtener el pago por su ID
        Payment payment = Payment.get(apiContext, paymentId);
        // Ejecutar el pago
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        Payment executedPayment = payment.execute(apiContext, paymentExecution);
        // Verificar el estado del pago
        if (executedPayment.getState().equals("approved")) {
            // Obtener detalles del pago
            Transaction transaction = executedPayment.getTransactions().get(0);
            String descripcion = transaction.getDescription();
            String[] partes = descripcion.split(":");
            Integer idPedido = Integer.parseInt(partes[1].trim());
            
            // Buscar el pedido
            Pedido pedido = pedidoRepositorio.findById(idPedido)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
            
            // Crear y guardar la transacción
            Transaccion transaccion = new Transaccion();
            transaccion.setPedido(pedido);
            transaccion.setMontoTotal(Double.parseDouble(transaction.getAmount().getTotal()));
            transaccion.setMetodoPago("PayPal");
            transaccion.setEstado("Completado");
            
            transaccionRepositorio.save(transaccion);
            
            // Redirigir a una página de confirmación en tu tienda
            return "redirect:http://localhost:3000/confirmacion-pago?idPedido=" + idPedido;
        } else {
            // Redirigir a una página de error en tu tienda
            return "redirect:http://localhost:3000/error-pago";
        }
    }
}