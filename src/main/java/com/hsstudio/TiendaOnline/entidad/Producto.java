/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.entidad;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;




@Entity
public class Producto {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       @Column(name = "Id_Producto") // Asegúrate de importar javax.persistence.Column
    private Integer Id_Producto;

    private String nombre;
    private String descripcion;
    private float precio;
    private int talla;
    private String color;
    private String genero;
    private Integer cantidad;

    // Constructor vacío (necesario para JPA)
    public Producto() {
    }

    public Producto(String nombre, String descripcion, String color, float precio, int talla, String genero, int cantidad) {
    this.nombre = nombre;
    this.descripcion = descripcion;
    this.color = color;
    this.precio = precio;
    this.talla = talla;
    this.genero = genero;
    this.cantidad = cantidad;
}


    // Getters y setters
    public Integer  getId() {
        return Id_Producto;
    }

    public void setId(Integer  id) {
        this.Id_Producto = Id_Producto;
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

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}


