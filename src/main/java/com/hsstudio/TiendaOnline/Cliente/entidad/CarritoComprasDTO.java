package com.hsstudio.TiendaOnline.Cliente.entidad;

import com.hsstudio.TiendaOnline.Admin.entidad.ProductoDTO;
import java.util.List;

/**
 *
 * @author alexa
 */
public class CarritoComprasDTO {
    private Integer idCarrito;
    private Integer numeroProductos;
    private Float precioTotal;
    private List<ProductoDTO> productos;

    public CarritoComprasDTO(Integer idCarrito, Integer numeroProductos, Float precioTotal, List<ProductoDTO> productos) {
        this.idCarrito = idCarrito;
        this.numeroProductos = numeroProductos;
        this.precioTotal = precioTotal;
        this.productos = productos;
    }

    public Integer getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Integer idCarrito) {
        this.idCarrito = idCarrito;
    }

    public Integer getNumeroProductos() {
        return numeroProductos;
    }

    public void setNumeroProductos(Integer numeroProductos) {
        this.numeroProductos = numeroProductos;
    }

    public Float getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(Float precioTotal) {
        this.precioTotal = precioTotal;
    }

    public List<ProductoDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoDTO> productos) {
        this.productos = productos;
    }
    
    
}