package com.hsstudio.TiendaOnline.Cliente.controlador;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.entidad.ProductoDTO;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;

import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoComprasDTO;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;


@RestController
@RequestMapping("/carrito-compras")
public class CarritoComprasControlador {

    private final CarritoComprasRepositorio carritoComprasRepositorio;
    private final ProductoRepositorio productoRepositorio;

    @Autowired
    public CarritoComprasControlador(CarritoComprasRepositorio carritoComprasRepositorio, ProductoRepositorio productoRepositorio) {
        this.carritoComprasRepositorio = carritoComprasRepositorio;
        this.productoRepositorio = productoRepositorio;
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

    private CarritoComprasDTO convertirACarritoComprasDTO(CarritoCompras carrito) {
    List<ProductoDTO> productosDTO = carrito.getProductos().stream()
        .map(producto -> new ProductoDTO(producto.getIdProducto(), producto.getNombre(), producto.getDescripcion(), producto.getPrecio(), producto.getTalla(), producto.getColor(), producto.getGenero(), producto.getTipoZapato(), producto.getImagen()))
        .collect(Collectors.toList());

    return new CarritoComprasDTO(carrito.getIdCarrito(), carrito.getNumeroProductos(), carrito.getPrecioTotal(), productosDTO);
}


    private void setCarritoIdCookie(HttpServletResponse response, String carritoId) {
        Cookie cookie = new Cookie("carritoId", carritoId);
        cookie.setMaxAge(24 * 60 * 60); // Establecer la duración de la cookie a 24 horas
        response.addCookie(cookie);
    }
// ... (resto del código)

class MensajeRespuesta {
    private String mensaje;

    public MensajeRespuesta(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}
    
    // Obtener todos los carritos
    @GetMapping
    public List<CarritoCompras> obtenerTodosLosCarritos() {
        return carritoComprasRepositorio.findAll();
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
public ResponseEntity<Object> agregarProducto(@RequestBody Integer idProducto, HttpServletRequest request, HttpServletResponse response) {
    // Leer la cookie del carrito
    String carritoId = getCarritoIdFromCookie(request);

    // Si no existe un carrito en la cookie, crear uno nuevo
    if (carritoId == null) {
        carritoId = UUID.randomUUID().toString();
        setCarritoIdCookie(response, carritoId);
    }

    // Guardar el valor de "carritoId" en la sesión
    request.getSession().setAttribute("carritoId", carritoId);

    // Buscar el carrito en la base de datos
    CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(carritoId);

    if (carrito == null) {
        // Si no existe el carrito, enviar un mensaje de error
        String mensaje = "No se encontró el carrito de compras. Por favor, inténtelo de nuevo.";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeRespuesta(mensaje));
    }

    // Buscar el producto completo en la base de datos
    Producto producto = productoRepositorio.findById(idProducto)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    // Agregar el producto al carrito
    carrito.getProductos().add(producto);

    // Actualizar el número de productos y el precio total
    carrito.setNumeroProductos(carrito.getProductos().size());
    carrito.setPrecioTotal(carrito.getProductos().stream().map(Producto::getPrecio).reduce(0f, Float::sum));

    // Guardar el carrito actualizado en la base de datos
    carritoComprasRepositorio.save(carrito);

    // Convertir a DTO
    CarritoComprasDTO carritoDTO = convertirACarritoComprasDTO(carrito);

    return ResponseEntity.ok(carritoDTO);
}


   @DeleteMapping("/eliminar-producto/{idProducto}")
public CarritoComprasDTO eliminarProducto(@PathVariable Integer idProducto, HttpServletRequest request, HttpServletResponse response) {
    // Leer la cookie del carrito
    String carritoId = getCarritoIdFromCookie(request);

    // Si no existe un carrito en la cookie, lanzar una excepción
    if (carritoId == null) {
        throw new RuntimeException("No hay un carrito en la sesión actual");
    }

    // Buscar el carrito en la base de datos
    CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(carritoId);
    if (carrito == null) {
        throw new RuntimeException("No se encontró el carrito con el ID de sesión proporcionado");
    }

    // Buscar el producto completo en la base de datos
    Producto producto = productoRepositorio.findById(idProducto)
        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    // Eliminar el producto del carrito
    carrito.getProductos().remove(producto);

    // Actualizar el número de productos y el precio total
    carrito.setNumeroProductos(carrito.getProductos().size());
    carrito.setPrecioTotal(carrito.getProductos().stream().map(Producto::getPrecio).reduce(0f, Float::sum));

    // Guardar el carrito actualizado en la base de datos
    carritoComprasRepositorio.save(carrito);

    // Convertir a DTO
    CarritoComprasDTO carritoDTO = convertirACarritoComprasDTO(carrito);

    return carritoDTO;
}



    
    @GetMapping("/productos")
public CarritoComprasDTO obtenerProductosCarrito(HttpServletRequest request) {
    // Obtener el ID del carrito de la cookie
    String carritoId = getCarritoIdFromCookie(request);

    // Si no hay un carrito en la cookie, lanzar una excepción
    if (carritoId == null) {
        throw new RuntimeException("No hay un carrito en la sesión actual");
    }

    // Buscar el carrito en la base de datos
    CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(carritoId);
    if (carrito == null) {
        throw new RuntimeException("No se encontró el carrito con el ID de sesión proporcionado");
    }

    // Convertir a DTO y devolver
    return convertirACarritoComprasDTO(carrito);
}


}