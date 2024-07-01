package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.Pedido;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.TransaccionRepositorio;
import com.hsstudio.TiendaOnline.Pago.PaypalConfig;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class PedidoControladorTest {

    @InjectMocks
    private PedidoControlador pedidoControlador;

    @Mock
    private PedidoRepositorio pedidoRepositorio;

    @Mock
    private CarritoComprasRepositorio carritoComprasRepositorio;

    @Mock
    private TransaccionRepositorio transaccionRepositorio;

    @Mock
    private HttpServletRequest request;

    @Mock
    private APIContext apiContext;

    @Mock
    private CarritoComprasService carritoComprasService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Crear una instancia real de PaypalConfig
        PaypalConfig paypalConfig = new PaypalConfig();
        paypalConfig.setClientId("clientId");
        paypalConfig.setClientSecret("clientSecret");
        paypalConfig.setMode("sandbox");

        // Crear una instancia de APIContext con la configuración de PaypalConfig
        apiContext = new APIContext(paypalConfig.getClientId(), paypalConfig.getClientSecret(), paypalConfig.getMode());
        Map<String, String> configuration = new HashMap<>();
        configuration.put("mode", paypalConfig.getMode());
        configuration.put("http.ConnectionTimeOut", "1000");
        configuration.put("http.Retry", "1");
        configuration.put("service.EndPoint", "https://api-m.sandbox.paypal.com");
        apiContext.setConfigurationMap(configuration);
    }



    // Agrega más métodos de prueba para los otros métodos del controlador
}