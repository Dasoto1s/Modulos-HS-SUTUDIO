package com.hsstudio.TiendaOnline.controlador;

import com.hsstudio.TiendaOnline.entidad.Producto;
import com.hsstudio.TiendaOnline.repositorio.ProductoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoControlador {

    private final ProductoRepositorio productoRepositorio;

    @Autowired
    public ProductoControlador(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }

    // Obtener todos los productos
    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoRepositorio.findAll();
    }

    // Crear un nuevo producto
    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoRepositorio.save(producto);
    }

    // Actualizar un producto existente
    @PutMapping("/{Id_Producto}")
    public Producto actualizarProducto(@PathVariable Integer  Id_Producto, @RequestBody Producto productoActualizado) {
        return productoRepositorio.findById(Id_Producto)
                .map(producto -> {
                    producto.setNombre(productoActualizado.getNombre());
                    producto.setDescripcion(productoActualizado.getDescripcion());
                    producto.setColor(productoActualizado.getColor());
                    producto.setPrecio(productoActualizado.getPrecio());
                    producto.setTalla(productoActualizado.getTalla());
                    producto.setGenero(productoActualizado.getGenero());
                    producto.setCantidad(productoActualizado.getCantidad());
                    return productoRepositorio.save(producto);
                })
                .orElseGet(() -> {
                    productoActualizado.setId(Id_Producto);
                    return productoRepositorio.save(productoActualizado);
                });
    }

    // Eliminar un producto
    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Integer  Id_Producto) {
        productoRepositorio.deleteById(Id_Producto);
    }
}
