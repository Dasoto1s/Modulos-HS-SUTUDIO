package com.hsstudio.TiendaOnline.Admin.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.CrearProductoRequest;
import com.hsstudio.TiendaOnline.Admin.entidad.Inventario;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.repositorio.InventarioRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/productos")
public class ProductoControlador {
    
    private final ProductoRepositorio productoRepositorio;
    private final InventarioRepositorio inventarioRepositorio;

    @Autowired
    public ProductoControlador(ProductoRepositorio productoRepositorio, InventarioRepositorio inventarioRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.inventarioRepositorio = inventarioRepositorio;
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




}
