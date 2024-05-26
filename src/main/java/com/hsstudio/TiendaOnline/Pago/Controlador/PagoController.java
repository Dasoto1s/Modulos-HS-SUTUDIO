package com.hsstudio.TiendaOnline.Pago.Controlador;

import com.hsstudio.TiendaOnline.Pago.PaypalConfig;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.paypal.api.payments.Links;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private final PaypalConfig paypalConfig;

    public PagoController(PaypalConfig paypalConfig) {
        this.paypalConfig = paypalConfig;
    }

   @PostMapping("/procesar-pago")
public String procesarPago(@RequestParam("monto") double monto) throws PayPalRESTException {
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
    transaction.setDescription("Pago de ejemplo");
    payment.setTransactions(List.of(transaction));

    // Configurar las URL de retorno y cancelación
    RedirectUrls redirectUrls = new RedirectUrls();
    redirectUrls.setCancelUrl("http://localhost:8080/pagos/pago-cancelado");
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
    return "redirect:" + approvalUrl;
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
        // El pago se completó exitosamente
        // Realiza las acciones necesarias (actualizar la base de datos, enviar confirmación, etc.)
        return "Pago exitoso";
    } else {
        // El pago no se completó exitosamente
        return "Error al procesar el pago";
    }
}
}