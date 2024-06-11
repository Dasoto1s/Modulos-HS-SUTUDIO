package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.Pedido;
import com.hsstudio.TiendaOnline.Cliente.entidad.Transaccion;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.TransaccionRepositorio;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.util.Date;
import java.util.List;
import com.paypal.api.payments.Links;
import org.hibernate.Hibernate;

@RestController
@RequestMapping("/pedidos")
public class PedidoControlador {

    private final PedidoRepositorio pedidoRepositorio;
    private final CarritoComprasRepositorio carritoComprasRepositorio;
    private final TransaccionRepositorio transaccionRepositorio;
    private final APIContext apiContext;

    @Autowired
    public PedidoControlador(PedidoRepositorio pedidoRepositorio, CarritoComprasRepositorio carritoComprasRepositorio,
                             TransaccionRepositorio transaccionRepositorio, APIContext apiContext) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.carritoComprasRepositorio = carritoComprasRepositorio;
        this.transaccionRepositorio = transaccionRepositorio;
        this.apiContext = apiContext;
    }

    private String getCarritoIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("carritoId")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

 @PostMapping("/crear-pedido")
    public ResponseEntity<Void> crearPedido(@RequestParam String direccionEnvio,
                                            @RequestParam String departamento,
                                            @RequestParam String ciudad,
                                            HttpServletRequest request) {
        // Obtener el valor de "carritoId" de la sesión
        HttpSession session = request.getSession();
        String carritoId = (String) session.getAttribute("carritoId");

        if (carritoId != null) {
            // Buscar el carrito de compras en la base de datos por el campo session_id
           CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(carritoId);
             Hibernate.initialize(carrito.getProductos());

            if (carrito != null) {
                // Crear un nuevo objeto Pedido
                Pedido pedido = new Pedido();

                // Asignar el objeto CarritoCompras al pedido
                pedido.setCarritoCompras(carrito);

                // Asignar los campos del pedido con los datos recibidos en los parámetros
                pedido.setDireccionEnvio(direccionEnvio);
                pedido.setFechaPedido(new Date()); // Generar la fecha actual automáticamente
                pedido.setDepartamento(departamento);
                pedido.setCiudad(ciudad);

                // Guardar el pedido en la base de datos
                Pedido pedidoGuardado = pedidoRepositorio.save(pedido);

                // Construir la URL de redirección a la página de inicio de pago de PayPal
                String redirectUrl = "/pedidos/iniciar-pago/" + pedidoGuardado.getNumeroPedido();

                // Crear un objeto HttpHeaders y establecer la URL de redirección en el encabezado "Location"
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(URI.create(redirectUrl));

                // Devolver una respuesta con el estado HTTP 302 (Found) y el encabezado "Location"
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            } else {
                throw new RuntimeException("No se encontró el carrito de compras");
            }
        } else {
            throw new RuntimeException("No se encontró un valor para 'carritoId' en la sesión");
        }
    }

    @GetMapping("/iniciar-pago/{idPedido}")
    public String iniciarPago(@PathVariable Integer idPedido) {
        // Buscar el pedido por su ID en la base de datos
        Pedido pedido = pedidoRepositorio.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("No se encontró el pedido"));

        // Obtener el objeto CarritoCompras asociado al pedido
        CarritoCompras carrito = pedido.getCarritoCompras();

        // Obtener el monto total del carrito
        double montoTotal = carrito.getPrecioTotal();

        // Crear el objeto Payment
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(new Payer().setPaymentMethod("paypal"));

        // Configurar la transacción
        Transaction transaction = new Transaction();
        transaction.setAmount(new Amount().setCurrency("USD").setTotal(String.valueOf(montoTotal)));
        transaction.setDescription("Pago de compra:" + idPedido); // Agregar el ID del pedido a la descripción
        payment.setTransactions(List.of(transaction));

        // Configurar las URL de retorno y cancelación
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:8080/pagos/pago-cancelado");
        redirectUrls.setReturnUrl("http://localhost:8080/pagos/pago-exitoso");
        payment.setRedirectUrls(redirectUrls);

        // Crear el pago utilizando el objeto APIContext inyectado
        Payment createdPayment;
        try {
            createdPayment = payment.create(apiContext);
        } catch (PayPalRESTException e) {
            // Manejo de la excepción de PayPal
            e.printStackTrace();
            // Puedes lanzar una excepción personalizada o devolver un mensaje de error adecuado
            throw new RuntimeException("Error al crear el pago con PayPal", e);
        }

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
public String pagoExitoso(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
    try {
        System.out.println("Verificando pago con paymentId: " + paymentId);

        // Obtener los detalles del pago utilizando el ID de pago
        Payment payment = Payment.get(apiContext, paymentId);
        System.out.println("Pago obtenido: " + payment);

        // Verificar el estado del pago
        if (payment.getState().equals("approved")) {
            System.out.println("Pago aprobado");

            // El pago fue aprobado exitosamente
            double totalAmount = Double.parseDouble(payment.getTransactions().get(0).getAmount().getTotal());
            String paymentMethod = payment.getPayer().getPaymentMethod();

            // Obtener el ID del pedido desde la descripción del pago
            String paymentDescription = payment.getTransactions().get(0).getDescription();
            String[] descriptionParts = paymentDescription.split(":");
            Integer idPedido = Integer.parseInt(descriptionParts[1].trim());

            // Obtener el objeto Pedido correspondiente
            Pedido pedido = pedidoRepositorio.findById(idPedido)
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

            // Crear una nueva instancia de Transaccion y establecer los detalles del pago
            Transaccion transaccion = new Transaccion();
            transaccion.setPedido(pedido); // Asignar el objeto Pedido
            transaccion.setMontoTotal(totalAmount);
            transaccion.setMetodoPago(paymentMethod);
            transaccion.setEstado("pagado"); // Establecer el estado de la transacción a "pagado"

            // Guardar la transacción en la base de datos
            Transaccion transaccionGuardada = transaccionRepositorio.save(transaccion);
            System.out.println("Transacción guardada: " + transaccionGuardada);

            // Renderizar una vista de éxito o redirigir a una página de confirmación
            return "pago-exitoso";
        } else {
            System.out.println("Pago no aprobado. Estado: " + payment.getState());
            // El pago no fue aprobado o hubo un error
            return "pago-error";
        }
    } catch (PayPalRESTException e) {
        System.out.println("Error en PayPal: " + e.getMessage());
        // Manejar la excepción de PayPal
        e.printStackTrace();
        return "pago-error";
    }
}
}