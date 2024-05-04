/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Cliente.entidad.Promociones;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/promociones")
public class PromocionesControlador {

    private final PromocionesRepositorio promocionesRepositorio;
    private final ProductoRepositorio productoRepositorio;

    @Autowired
    public PromocionesControlador(PromocionesRepositorio promocionesRepositorio, ProductoRepositorio productoRepositorio) {
        this.promocionesRepositorio = promocionesRepositorio;
        this.productoRepositorio = productoRepositorio;
    }

    // Obtener productos en promoción
    @GetMapping
    public List<Producto> obtenerProductosEnPromocion() {
        List<Promociones> productosEnPromocion = promocionesRepositorio.findAll();
        return productosEnPromocion.stream()
                .map(Promociones::getProducto)
                .collect(Collectors.toList());
    }

    // Agregar un producto a la lista de promociones
    @PostMapping("/{productoId}")
    public ResponseEntity<String> agregarProductoEnPromocion(@PathVariable Integer productoId, @RequestParam BigDecimal descuento) {
        Optional<Producto> productoOptional = productoRepositorio.findById(productoId);
        if (productoOptional.isPresent()) {
            Promociones productoEnPromocion = new Promociones();
            productoEnPromocion.setProducto(productoOptional.get());
            productoEnPromocion.setDescuento(descuento); // Ejemplo: 0.10 para 10% de descuento
            promocionesRepositorio.save(productoEnPromocion);
            return ResponseEntity.ok("Producto agregado a promociones");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar un producto de la lista de promociones
    @DeleteMapping("/{productoId}")
    public ResponseEntity<String> eliminarProductoEnPromocion(@PathVariable Integer productoId) {
        Optional<Promociones> productoEnPromocionOptional = promocionesRepositorio.findById(productoId);
        if (productoEnPromocionOptional.isPresent()) {
            promocionesRepositorio.delete(productoEnPromocionOptional.get());
            return ResponseEntity.ok("Producto eliminado de promociones");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

