package com.hsstudio.TiendaOnline.Admin.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import com.hsstudio.TiendaOnline.Admin.entidad.CrearProductoRequest;
import com.hsstudio.TiendaOnline.Admin.entidad.Inventario;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.InventarioRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/productos")
public class ProductoControlador {
    
    private final ProductoRepositorio productoRepositorio;
    private final InventarioRepositorio inventarioRepositorio;
    private final AdminRepositorio adminRepositorio;

    @Autowired
    public ProductoControlador(ProductoRepositorio productoRepositorio, InventarioRepositorio inventarioRepositorio, AdminRepositorio adminRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.inventarioRepositorio = inventarioRepositorio;
        this.adminRepositorio = adminRepositorio;
    }

    // Obtener todos los productos
    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoRepositorio.findAll();
    }

    // Obtener un producto por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Integer id) {
        Optional<Producto> productoOptional = productoRepositorio.findById(id);
        return productoOptional.map(ResponseEntity::ok)
                               .orElseGet(() -> ResponseEntity.notFound().build());
    }

   // Crear un nuevo producto
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody CrearProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setTalla(request.getTalla());
        producto.setColor(request.getColor());
        producto.setGenero(request.getGenero());

        Inventario inventario = new Inventario();
        inventario.setCantidad(request.getCantidad());
        inventario.setStock(request.getStock());
        inventario.setCantidad_minima_requerida(request.getCantidad_minima_requerida());

        producto.setInventario(inventario); // Establecer la asociación
        inventario.setProducto(producto); // Establecer la asociación inversa

        Producto productoGuardado = productoRepositorio.save(producto); // Guardar el producto y su inventario asociado

        return ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado);
    }

    // Actualizar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Integer id, @RequestBody Producto productoActualizado) {
        return productoRepositorio.findById(id)
                .map(producto -> {
                    producto.setNombre(productoActualizado.getNombre());
                    producto.setDescripcion(productoActualizado.getDescripcion());
                    producto.setColor(productoActualizado.getColor());
                    producto.setPrecio(productoActualizado.getPrecio());
                    producto.setTalla(productoActualizado.getTalla());
                    producto.setGenero(productoActualizado.getGenero());

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
                productoRepositorio.delete(producto); // Esto debería eliminar también el inventario asociado
                return ResponseEntity.ok().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Inicio de sesión del administrador
    @PostMapping("/admin/login")
    public ResponseEntity<?> login(@RequestBody Admin admin) {
        Admin existingAdmin = adminRepositorio.findByEmail(admin.getEmail());
        if (existingAdmin != null && existingAdmin.getPassword().equals(admin.getPassword())) {
            // Genera la clave segura
            SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

            // Genera el token
            String token = Jwts.builder()
                .setSubject(existingAdmin.getEmail())
                .setIssuedAt(new Date())
                .signWith(key)
                .compact();

            // Devuelve el token
            return ResponseEntity.ok().body(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Correo electrónico o contraseña incorrectos");
        }
    }
}
