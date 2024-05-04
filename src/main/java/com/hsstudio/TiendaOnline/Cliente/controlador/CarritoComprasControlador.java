/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.Cliente.controlador;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public CarritoCompras actualizarCarrito(@PathVariable Integer id, @RequestBody CarritoCompras carritoActualizado) {
        return carritoComprasRepositorio.findById(id)
                .map(carrito -> {
                    carrito.setNumeroProductos(carritoActualizado.getNumeroProductos());
                    carrito.setPrecioTotal(carritoActualizado.getPrecioTotal());
                    
                    return carritoComprasRepositorio.save(carrito);
                })
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
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
public CarritoCompras agregarProducto(@RequestBody Integer idProducto, HttpSession httpSession) {
    // Obtener el carrito de la sesión
    CarritoCompras carrito = (CarritoCompras) httpSession.getAttribute("carrito");

    // Si no existe un carrito en la sesión, crear uno nuevo
    if (carrito == null) {
        carrito = new CarritoCompras();
        httpSession.setAttribute("carrito", carrito);
    }

    // Buscar el producto completo en la base de datos
    Producto producto = productoRepositorio.findById(idProducto)
        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    // Agregar el producto al carrito
    carrito.getProductos().add(producto);

    return carrito;
}




@PostMapping("/eliminar-producto")
public CarritoCompras eliminarProducto(@RequestBody Integer idProducto, HttpSession httpSession) {
    // Obtener el carrito de la sesión
    CarritoCompras carrito = (CarritoCompras) httpSession.getAttribute("carrito");

    // Si el carrito es null, devolver un nuevo carrito vacío
    if (carrito == null) {
        return new CarritoCompras();
    }

    // Buscar el producto completo en la base de datos
    Producto producto = productoRepositorio.findById(idProducto)
        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    // Eliminar el producto del carrito
    carrito.getProductos().remove(producto);

    return carrito;
}







}
