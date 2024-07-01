package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Inventario;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.entidad.ProductoDTO;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoComprasDTO;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/carrito-compras")
public class CarritoComprasControlador {

    private final CarritoComprasRepositorio carritoComprasRepositorio;
    private final ProductoRepositorio productoRepositorio;
      private CarritoComprasService carritoComprasService;

    @Autowired
    public CarritoComprasControlador(CarritoComprasRepositorio carritoComprasRepositorio, ProductoRepositorio productoRepositorio, CarritoComprasService carritoComprasService) {
        this.carritoComprasRepositorio = carritoComprasRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.carritoComprasService = carritoComprasService;
        
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

                return dto;
            })
            .collect(Collectors.toList());

    return new CarritoComprasDTO(carrito.getIdCarrito(), carrito.getNumeroProductos(), carrito.getPrecioTotal(), productosDTO);
}

    private void setCarritoIdCookie(HttpServletResponse response, String carritoId) {
        Cookie cookie = new Cookie("carritoId", carritoId);
        cookie.setMaxAge(24 * 60 * 60); // Establecer la duración de la cookie a 24 horas
        response.addCookie(cookie);
    }

    // Obtener todos los carritos
    @GetMapping("/productos")
public ResponseEntity<CarritoComprasDTO> obtenerProductosCarrito(HttpServletRequest request) {
    // Obtener el ID de sesión del cliente
    String sessionId = getSessionIdFromHeader(request);

    // Buscar el carrito de compras asociado al ID de sesión
    CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(sessionId);
    if (carrito == null) {
        return ResponseEntity.notFound().build();
    }

    // Convertir a DTO y devolver
    CarritoComprasDTO carritoDTO = convertirACarritoComprasDTO(carrito);
    return ResponseEntity.ok(carritoDTO);
}

    // Obtener un carrito por su ID
    @GetMapping("/{id}")
    public CarritoCompras obtenerCarritoPorId(@PathVariable Integer id) {
        Optional<CarritoCompras> carrito = carritoComprasRepositorio.findById(id);
        return carrito.orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
    }

    // Crear un nuevo carrito
    @PostMapping
    public CarritoCompras crearCarrito(@RequestBody CarritoCompras nuevoCarrito) {
        return carritoComprasRepositorio.save(nuevoCarrito);
    }

    // Actualizar un carrito existente
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
            throw new RuntimeException("Carrito no encontrado");
        }
    }

    // Eliminar un carrito
    @DeleteMapping("/{id}")
    public void eliminarCarrito(@PathVariable Integer id) {
        if (carritoComprasRepositorio.existsById(id)) {
            carritoComprasRepositorio.deleteById(id);
        } else {
            throw new RuntimeException("Carrito no encontrado");
        }
    }

@PostMapping("/agregar-producto")
@Transactional
public ResponseEntity<CarritoComprasDTO> agregarProducto(@RequestBody Integer idProducto, HttpServletRequest request) {
    String sessionId = getSessionIdFromHeader(request);
    System.out.println("Session ID recibido: " + sessionId);

    CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(sessionId);
    if (carrito == null) {
        System.out.println("Creando nuevo carrito para session ID: " + sessionId);
        carrito = new CarritoCompras();
        carrito.setSessionId(sessionId);
        carrito.setNumeroProductos(0);
        carrito.setPrecioTotal(0f);
        carrito.setFechaCreacion(new Date()); // Establecer la fecha de creación
    } else {
        System.out.println("Carrito existente encontrado con ID: " + carrito.getIdCarrito());
    }

    Producto producto = productoRepositorio.findById(idProducto)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    carrito.getProductos().add(producto);
    carrito.setNumeroProductos(carrito.getProductos().size());
    carrito.setPrecioTotal(carrito.getProductos().stream().map(Producto::getPrecio).reduce(0f, Float::sum));

    carritoComprasRepositorio.save(carrito);

    CarritoComprasDTO carritoDTO = convertirACarritoComprasDTO(carrito);
    return ResponseEntity.ok(carritoDTO);
}
  @DeleteMapping("/eliminar-producto/{idProducto}")
public ResponseEntity<CarritoComprasDTO> eliminarProducto(@PathVariable Integer idProducto, HttpServletRequest request, HttpServletResponse response) {
    // Obtener el ID de sesión del cliente desde la cookie
    String sessionId = getSessionIdFromHeader(request);
    System.out.println("ID de sesión: " + sessionId);

    // Buscar el carrito en la base de datos utilizando el ID de sesión
    CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(sessionId);
    if (carrito == null) {
        System.out.println("Carrito no encontrado para el ID de sesión: " + sessionId);
        return ResponseEntity.notFound().build();
    }

    // Buscar el producto en el carrito
    Optional<Producto> productoOptional = carrito.getProductos().stream()
            .filter(producto -> producto.getIdProducto().equals(idProducto))
            .findFirst();

    if (productoOptional.isPresent()) {
        Producto producto = productoOptional.get();
        System.out.println("Producto encontrado: " + producto.getNombre());

        // Eliminar el producto del carrito
        carrito.getProductos().remove(producto);

        // Actualizar el número de productos y el precio total
        carrito.setNumeroProductos(carrito.getProductos().size());
        carrito.setPrecioTotal(carrito.getProductos().stream().map(Producto::getPrecio).reduce(0f, Float::sum));

        // Guardar el carrito actualizado en la base de datos
        carritoComprasRepositorio.save(carrito);

        // Convertir a DTO
        CarritoComprasDTO carritoDTO = convertirACarritoComprasDTO(carrito);

        return ResponseEntity.ok(carritoDTO);
    } else {
        System.out.println("Producto no encontrado en el carrito para el ID: " + idProducto);
        return ResponseEntity.notFound().build();
    }
}

 @PostMapping("/limpiar")
    public ResponseEntity<?> limpiarCarrito(HttpServletRequest request) {
        String sessionId = getSessionIdFromHeader(request);
        if (sessionId == null) {
            return ResponseEntity.badRequest().body(new MensajeRespuesta("No se proporcionó un ID de sesión válido"));
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