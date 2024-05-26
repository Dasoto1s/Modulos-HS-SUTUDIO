package com.hsstudio.TiendaOnline.Admin.controlador;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hsstudio.TiendaOnline.Admin.entidad.*;
import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.InventarioRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@RestController
@RequestMapping("/productos")
public class ProductoControlador {

    private final ProductoRepositorio productoRepositorio;
    private final InventarioRepositorio inventarioRepositorio;
    private final AdminRepositorio adminRepositorio;

    private final SecretKey secretKey;

    @Autowired
    public ProductoControlador(ProductoRepositorio productoRepositorio,
                               InventarioRepositorio inventarioRepositorio,
                               AdminRepositorio adminRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.inventarioRepositorio = inventarioRepositorio;
        this.adminRepositorio = adminRepositorio;

        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    private Integer extraerAdminIdDesdeToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("adminId").asInt();
        } else {
            throw new JWTDecodeException("Token JWT inválido");
        }
    }

    // Obtener todos los productos
    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoRepositorio.findAll();
    }

    // Obtener un producto por su ID
  @PostMapping
public ResponseEntity<Object> crearProducto(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestParam("nombre") String nombre,
        @RequestParam("descripcion") String descripcion,
        @RequestParam("precio") float precio,
        @RequestParam("talla") String tallaStr,
        @RequestParam("color") String color,
        @RequestParam("genero") String genero,
        @RequestParam("tipoZapato") String tipoZapato,
        @RequestParam("imagen") MultipartFile imagen,
        @RequestParam("cantidad") int cantidad,
        @RequestParam("stock") int stock,
        @RequestParam("cantidadMinimaRequerida") int cantidadMinimaRequerida
) throws IOException {

    // Validar que todos los campos requeridos estén presentes
    if (nombre == null || nombre.isEmpty() ||
        descripcion == null || descripcion.isEmpty() ||
        tallaStr == null || tallaStr.isEmpty() ||
        color == null || color.isEmpty() ||
        genero == null || genero.isEmpty() ||
        tipoZapato == null || tipoZapato.isEmpty() ||
        imagen == null || imagen.isEmpty() ||
        cantidad < 0 || stock < 0 || cantidadMinimaRequerida < 0) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Todos los campos requeridos deben estar presentes y ser válidos."));
    }

    // Verificar la presencia del token JWT
    if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
        // Obtener el ID del administrador desde el token JWT
        Integer adminId = extraerAdminIdDesdeToken(authorizationHeader);

        // Buscar al administrador por su ID
        Optional<Admin> adminOptional = adminRepositorio.findById(adminId);

        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();

            // Validar la longitud de la talla
            if (tallaStr.length() != 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("La talla debe tener exactamente dos dígitos."));
            }

            // Convertir la talla a un entero
            int talla;
            try {
                talla = Integer.parseInt(tallaStr);
                if (talla <= 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("La talla debe ser un número positivo."));
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("La talla no es un número válido."));
            }

            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);
            producto.setTalla(talla);
            producto.setColor(color);
            producto.setGenero(genero);
            producto.setTipoZapato(tipoZapato);
            producto.setImagen(imagen.getBytes());

            Inventario inventario = new Inventario();
            inventario.setAdmin(admin);
            inventario.setCantidad(cantidad);
            inventario.setStock(stock);
            inventario.setCantidad_minima_requerida(cantidadMinimaRequerida);
            producto.setInventario(inventario);
            inventario.setProducto(producto);

            Producto productoGuardado = productoRepositorio.save(producto);

            return ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

    // Actualizar un producto existente
@PutMapping("/{id}")
public ResponseEntity<Producto> actualizarProducto(
    @PathVariable Integer id,
    @RequestParam(required = false) String nombre,
    @RequestParam(required = false) String descripcion,
    @RequestParam(required = false) String color,
    @RequestParam(required = false) Float precio,
    @RequestParam(required = false) Integer talla,
    @RequestParam(required = false) String genero,
    @RequestParam(required = false) String tipoZapato,
    @RequestParam(required = false) MultipartFile imagen,
    @RequestParam(required = false) Integer cantidad,
    @RequestParam(required = false) Integer stock,
    @RequestParam(required = false) Integer cantidadMinimaRequerida
) throws IOException {

    return (ResponseEntity<Producto>) productoRepositorio.findById(id)
        .map(producto -> {
            List<String> camposVacios = new ArrayList<>();

            if (nombre != null && nombre.isEmpty()) {
                camposVacios.add("El campo 'nombre' no puede estar vacío.");
            } else if (nombre != null) {
                producto.setNombre(nombre);
            }

            // Realizar validaciones similares para los demás campos...

            if (!camposVacios.isEmpty()) {
                String mensajeError = "Los siguientes campos no pueden estar vacíos: \n" + String.join("\n", camposVacios);
                return ResponseEntity.badRequest().body(new MensajeRespuesta(mensajeError));
            }

            Producto productoActualizadoGuardado = productoRepositorio.save(producto);
            return ResponseEntity.ok().body(productoActualizadoGuardado);
        })
        .orElseGet(() -> ResponseEntity.notFound().build());
}

   // Eliminar un producto
@DeleteMapping("/{id}")
public ResponseEntity<?> eliminarProducto(@PathVariable Integer id) {
    return productoRepositorio.findById(id)
        .map(producto -> {
            productoRepositorio.delete(producto);
            return ResponseEntity.ok("Producto eliminado con éxito");
        })
        .orElseGet(() -> ResponseEntity.notFound().build());
}

    // Inicio de sesión del administrador
    @PostMapping("/admin/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        Admin existingAdmin = adminRepositorio.findByEmail(email);

        if (existingAdmin != null && existingAdmin.getPassword().equals(password)) {
            String token = Jwts.builder()
                    .setSubject(existingAdmin.getEmail())
                    .claim("adminId", existingAdmin.getId())
                    .setIssuedAt(new Date())
                    .signWith(secretKey)
                    .compact();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Inicio de sesión exitoso");
            response.put("token", token);

            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo electrónico o contraseña incorrectos");
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<Object> buscarProductos(@RequestParam(required = false) String palabrasClave,
                                                  @RequestParam(required = false) Integer talla) {
        List<Producto> productos;
        if (palabrasClave != null && talla != null) {
            productos = productoRepositorio.findByNombreContainingIgnoreCaseAndTalla(palabrasClave, talla);
        } else if (palabrasClave != null) {
            productos = productoRepositorio.buscarPorNombreIgnoreCaseNativo(palabrasClave);
        } else if (talla != null) {
            productos = productoRepositorio.findByTalla(talla);
        } else {
            productos = productoRepositorio.findAll();
        }

        if (productos.isEmpty()) {
            return ResponseEntity.ok(new MensajeRespuesta("No se encontraron productos para la búsqueda especificada."));
        }

        List<ProductoDTO> productoDTOs = productos.stream()
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
                    dto.setImagen(producto.getImagen());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(productoDTOs);
    }
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

class ErrorResponse {
    private String mensaje;

    public ErrorResponse(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }
}