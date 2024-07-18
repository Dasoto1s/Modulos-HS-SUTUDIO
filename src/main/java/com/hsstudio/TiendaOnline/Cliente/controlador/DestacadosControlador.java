/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.entidad.Destacados;
import com.hsstudio.TiendaOnline.Cliente.repositorio.DestacadosRepositorio;
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
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/destacados")
public class DestacadosControlador {

    private final DestacadosRepositorio destacadosRepositorio;
    private final ProductoRepositorio productoRepositorio;

    @Autowired
    public DestacadosControlador(DestacadosRepositorio destacadosRepositorio, ProductoRepositorio productoRepositorio) {
        this.destacadosRepositorio = destacadosRepositorio;
        this.productoRepositorio = productoRepositorio;
    }

    // Obtener productos destacados
 @GetMapping
public List<Destacados> obtenerProductosDestacados() {
    return destacadosRepositorio.findAll();
}


    // Agregar un producto a la lista de destacados
    @PostMapping("/{productoId}")
    public ResponseEntity<String> agregarProductoDestacado(@PathVariable Integer productoId) {
        if (destacadosRepositorio.count() > 12) {
            return ResponseEntity.badRequest().body("No se pueden agregar más de 12 productos destacados.");
        }

        Optional<Producto> productoOptional = productoRepositorio.findById(productoId);
        if (productoOptional.isPresent()) {
            Destacados productoDestacado = new Destacados();
            productoDestacado.setProducto(productoOptional.get());
            destacadosRepositorio.save(productoDestacado);
            return ResponseEntity.ok("Producto agregado a destacados");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

  
   // Eliminar un producto de la lista de destacados
@DeleteMapping("/{destacadoId}")
public ResponseEntity<String> eliminarProductoDestacado(@PathVariable Integer destacadoId) {
    Optional<Destacados> productoDestacadoOptional = destacadosRepositorio.findById(destacadoId);
    if (productoDestacadoOptional.isPresent()) {
        destacadosRepositorio.delete(productoDestacadoOptional.get());
        return ResponseEntity.ok("Producto eliminado de destacados");
    } else {
       return ResponseEntity.notFound().build();
    }
}


}
