/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.Admin.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 *
 * @author alexa
 */
@Entity
public class CrearProductoRequest {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Producto")
    private Integer idProducto;

    private String nombre;
    private String descripcion;
    private float precio;
    private int talla;
    private String color;
    private String genero;
     private Float cantidad;
    private Integer stock;
    private Integer Cantidad_minima_requerida;

    public CrearProductoRequest(Integer idProducto, String nombre, String descripcion, float precio, int talla, String color, String genero, Float cantidad, Integer stock, Integer Cantidad_minima_requerida) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.talla = talla;
        this.color = color;
        this.genero = genero;
        this.cantidad = cantidad;
        this.stock = stock;
        this.Cantidad_minima_requerida = Cantidad_minima_requerida;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getTalla() {
        return talla;
    }

    public void setTalla(int talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getCantidad_minima_requerida() {
        return Cantidad_minima_requerida;
    }

    public void setCantidad_minima_requerida(Integer Cantidad_minima_requerida) {
        this.Cantidad_minima_requerida = Cantidad_minima_requerida;
    }
   
    
}
