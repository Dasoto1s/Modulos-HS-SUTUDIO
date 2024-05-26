/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.Cliente.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.entidad.ProductoDTO;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.entidad.Destacados;
import com.hsstudio.TiendaOnline.Cliente.entidad.Promociones;
import com.hsstudio.TiendaOnline.Cliente.repositorio.DestacadosRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pagina-principal")
public class PaginaPrincipalControlador {

    private final ProductoRepositorio productoRepositorio;

    @Autowired
    public PaginaPrincipalControlador(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }

    @Autowired
    private DestacadosRepositorio destacadosRepositorio;

    @Autowired
    private PromocionesRepositorio promocionesRepositorio;

    // Obtener productos por género
    @GetMapping("/productos/{genero}")
    public List<ProductoDTO> obtenerProductosPorGenero(@PathVariable String genero) {
        List<Producto> productos = productoRepositorio.findByGenero(genero);

        // Convertir a DTO
        List<ProductoDTO> productoDTOs = convertirAProductoDTO(productos);

        return productoDTOs;
    }

    // Obtener los productos por subcategoria
    @GetMapping("/productos/{genero}/{tipoZapato}")
    public List<ProductoDTO> obtenerProductosPorSubcategoria(
            @PathVariable String genero,
            @PathVariable String tipoZapato) {
        // Realizar la búsqueda de productos por género y subcategoría
        List<Producto> productos = productoRepositorio.findByGeneroAndTipoZapato(genero, tipoZapato);

        // Convertir a DTO
        List<ProductoDTO> productoDTOs = convertirAProductoDTO(productos);

        return productoDTOs;
    }

    // Obtener todos los productos
    @GetMapping("/productos")
    public List<ProductoDTO> obtenerTodosLosProductos() {
        List<Producto> productos = productoRepositorio.findAll();

        // Convertir a DTO
        List<ProductoDTO> productoDTOs = convertirAProductoDTO(productos);

        return productoDTOs;
    }

    // Obtener un producto por su ID con todos sus detalles
    @GetMapping("/producto/{id}")
    public ProductoDTO obtenerProductoPorId(@PathVariable Integer id) {
        Producto producto = productoRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Convertir a DTO
        ProductoDTO productoDTO = convertirAProductoDTO(producto);

        return productoDTO;
    }

    @GetMapping("/productos/destacados")
    public List<ProductoDTO> obtenerProductosDestacados() {
        List<Producto> productos = destacadosRepositorio.findAll().stream()
                .map(Destacados::getProducto)
                .collect(Collectors.toList());

        // Convertir a DTO
        List<ProductoDTO> productoDTOs = convertirAProductoDTO(productos);

        return productoDTOs;
    }

    @GetMapping("/productos/promociones")
    public List<ProductoDTO> obtenerProductosEnPromocion() {
        List<Producto> productos = promocionesRepositorio.findAll().stream()
                .map(Promociones::getProducto)
                .collect(Collectors.toList());

        // Convertir a DTO
        List<ProductoDTO> productoDTOs = convertirAProductoDTO(productos);

        return productoDTOs;
    }

    private List<ProductoDTO> convertirAProductoDTO(List<Producto> productos) {
        return productos.stream()
                .map(this::convertirAProductoDTO)
                .collect(Collectors.toList());
    }

    private ProductoDTO convertirAProductoDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setIdProducto(producto.getIdProducto());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setTalla(producto.getTalla());
        dto.setColor(producto.getColor());
        dto.setGenero(producto.getGenero());
        dto.setTipoZapato(producto.getTipoZapato()); // Incluir la propiedad tipoZapato
        return dto;
    }
}