package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Inventario;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.entidad.ProductoDTO;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoComprasDTO;
import com.hsstudio.TiendaOnline.Cliente.entidad.Promociones;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import com.hsstudio.TiendaOnline.Admin.entidad.MensajeRespuesta;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/carrito-compras")
public class CarritoComprasControlador {

    private final CarritoComprasRepositorio carritoComprasRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final CarritoComprasService carritoComprasService;
    private final PromocionesRepositorio promocionesRepositorio;

    private static final String INVALID_SESSION_ID_PATTERN = "['\"%;]";

    @Autowired
    public CarritoComprasControlador(CarritoComprasRepositorio carritoComprasRepositorio,
                                     PromocionesRepositorio promocionesRepositorio,
                                     ProductoRepositorio productoRepositorio,
                                     CarritoComprasService carritoComprasService) {
        this.carritoComprasRepositorio = carritoComprasRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.carritoComprasService = carritoComprasService;
        this.promocionesRepositorio = promocionesRepositorio;
    }

    private boolean isSessionIdValid(String sessionId) {
        if (sessionId == null || Pattern.compile(INVALID_SESSION_ID_PATTERN).matcher(sessionId).find()) {
            return false;
        }
        return true;
    }

    private String getSessionIdFromHeader(HttpServletRequest request) {
        return request.getHeader("X-Session-Id");
    }

    private CarritoComprasDTO convertirACarritoComprasDTO(CarritoCompras carrito) {
        List<ProductoDTO> productosDTO = carrito.getProductos().stream()
                .map(producto -> {
                    ProductoDTO dto = new ProductoDTO();
                    dto.setIdProducto(producto.getIdProducto());
                    dto.setNombre(producto.getNombre());
                    dto.setDescripcion(producto.getDescripcion());
                    dto.setPrecio(producto.getPrecio());
                    dto.setTalla(producto.getTalla());
                    dto.setColor(producto.getColor());
                    dto.setGenero(producto.getGenero());
                    dto.setTipoZapato(producto.getTipoZapato());

                    Inventario inventario = producto.getInventario();
                    if (inventario != null) {
                        dto.setCantidad(inventario.getCantidad());
                        dto.setStock(inventario.getStock());
                        dto.setCantidadMinimaRequerida(inventario.getCantidad_minima_requerida());
                    }

                    dto.setImagen(producto.getImagen());

                    // Calcular el precio con descuento
                    Optional<Promociones> promocion = promocionesRepositorio.findByProductoIdProducto(producto.getIdProducto());
                    if (promocion.isPresent()) {
                        float descuento = promocion.get().getDescuento().floatValue();
                        float precioConDescuento = producto.getPrecio() * (1 - descuento / 100);
                        dto.setPrecioConDescuento(precioConDescuento);
                    } else {
                        dto.setPrecioConDescuento(producto.getPrecio());
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        // Calcular el precio total con descuentos
        float precioTotalConDescuentos = (float) productosDTO.stream()
                .mapToDouble(dto -> dto.getPrecioConDescuento())
                .sum();

        return new CarritoComprasDTO(carrito.getIdCarrito(), carrito.getNumeroProductos(), precioTotalConDescuentos, productosDTO);
    }
@GetMapping("/productos")
public ResponseEntity<Object> obtenerProductosCarrito(HttpServletRequest request) {
    String sessionId = getSessionIdFromHeader(request);
    if (sessionId == null || !isSessionIdValid(sessionId)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MensajeRespuesta("ID de sesión inválido"));
    }
    CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(sessionId);
    if (carrito == null) {
        return ResponseEntity.notFound().build();
    }
    CarritoComprasDTO carritoDTO = convertirACarritoComprasDTO(carrito);
    return ResponseEntity.ok(carritoDTO);
}

    @GetMapping("/{id}")
    public ResponseEntity<CarritoCompras> obtenerCarritoPorId(@PathVariable Integer id) {
        Optional<CarritoCompras> carrito = carritoComprasRepositorio.findById(id);
        return carrito.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CarritoCompras> crearCarrito(@RequestBody CarritoCompras nuevoCarrito) {
        CarritoCompras carritoCreado = carritoComprasRepositorio.save(nuevoCarrito);
        return ResponseEntity.status(HttpStatus.CREATED).body(carritoCreado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCarrito(@PathVariable Integer id, @RequestBody MultiValueMap<String, String> carritoActualizado) {
        Optional<CarritoCompras> optionalCarrito = carritoComprasRepositorio.findById(id);
        if (optionalCarrito.isPresent()) {
            CarritoCompras carrito = optionalCarrito.get();
            String numeroProductos = carritoActualizado.getFirst("numeroProductos");
            if (numeroProductos != null) {
                carrito.setNumeroProductos(Integer.parseInt(numeroProductos));
            }
            String precioTotal = carritoActualizado.getFirst("precioTotal");
            if (precioTotal != null) {
                carrito.setPrecioTotal(Float.parseFloat(precioTotal));
            }
            carritoComprasRepositorio.save(carrito);
            return ResponseEntity.ok("Carrito actualizado correctamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCarrito(@PathVariable Integer id) {
        if (carritoComprasRepositorio.existsById(id)) {
            carritoComprasRepositorio.deleteById(id);
            return ResponseEntity.ok("Carrito eliminado correctamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

   @PostMapping("/agregar-producto")
@Transactional
public ResponseEntity<?> agregarProducto(@RequestBody Integer idProducto, HttpServletRequest request) {
    // Validar y sanitizar el idProducto
    if (idProducto == null || idProducto <= 0) {
        return ResponseEntity.badRequest().body(new MensajeRespuesta("ID de producto inválido"));
    }

    String sessionId = getSessionIdFromHeader(request);
    if (sessionId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MensajeRespuesta("No se proporcionó un ID de sesión válido"));
    }

    CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(sessionId);
    if (carrito == null) {
        carrito = new CarritoCompras();
        carrito.setSessionId(sessionId);
        carrito.setNumeroProductos(0);
        carrito.setPrecioTotal(0f);
        carrito.setFechaCreacion(new Date());
    }

    Optional<Producto> productoOptional = productoRepositorio.findById(idProducto);
    if (productoOptional.isPresent()) {
        Producto producto = productoOptional.get();
        carrito.getProductos().add(producto);
        carrito.setNumeroProductos(carrito.getProductos().size());
        carrito.setPrecioTotal(carrito.getProductos().stream().map(Producto::getPrecio).reduce(0f, Float::sum));
        carritoComprasRepositorio.save(carrito);
        CarritoComprasDTO carritoDTO = convertirACarritoComprasDTO(carrito);
        return ResponseEntity.ok(carritoDTO);
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeRespuesta("Producto no encontrado"));
    }
}


@DeleteMapping("/eliminar-producto/{idProducto}")
public ResponseEntity<CarritoComprasDTO> eliminarProducto(@PathVariable Integer idProducto, HttpServletRequest request) {
    // Validar y sanitizar el idProducto
    if (idProducto == null || idProducto <= 0) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    String sessionId = getSessionIdFromHeader(request);

    if (sessionId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(sessionId);
    if (carrito == null) {
        return ResponseEntity.notFound().build();
    }

    Optional<Producto> productoOptional = carrito.getProductos().stream()
            .filter(producto -> producto.getIdProducto().equals(idProducto))
            .findFirst();

    if (productoOptional.isPresent()) {
        Producto producto = productoOptional.get();

        carrito.getProductos().remove(producto);
        carrito.setNumeroProductos(carrito.getProductos().size());
        carrito.setPrecioTotal(carrito.getProductos().stream().map(Producto::getPrecio).reduce(0f, Float::sum));

        carritoComprasRepositorio.save(carrito);

        CarritoComprasDTO carritoDTO = convertirACarritoComprasDTO(carrito);
        return ResponseEntity.ok(carritoDTO);
    } else {
        return ResponseEntity.notFound().build();
    }
}

  @PostMapping("/limpiar")
public ResponseEntity<?> limpiarCarrito(HttpServletRequest request) {
    String sessionId = getSessionIdFromHeader(request);
    if (sessionId == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MensajeRespuesta("No se proporcionó un ID de sesión válido"));
    }

    try {
        carritoComprasService.limpiarCarrito(sessionId);
        return ResponseEntity.ok(new MensajeRespuesta("Carrito limpiado con éxito"));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MensajeRespuesta("Error al limpiar el carrito: " + e.getMessage()));
    }
}

    private String obtenerSessionIdDesdeCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("sessionId")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    class MensajeRespuesta {
        private String mensaje;

        public MensajeRespuesta(String mensaje) {
            this.mensaje = mensaje;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}