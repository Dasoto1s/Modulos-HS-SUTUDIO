package com.hsstudio.TiendaOnline.Admin.entidad;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Producto implements Serializable {

    public static void setCarritos(Object object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Producto")
    private Integer idProducto;

    @Column(name = "nombre")
    private String nombre;

    private String descripcion;

    private float precio;

    private int talla;

    private String color;

    private String genero;

    @Lob
   @Column(name = "imagen", columnDefinition = "MEDIUMBLOB")
    private byte[] imagen;

    @Column(name = "tipo_zapato")
    private String tipoZapato;

@OneToOne(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonIgnore
private Inventario inventario;

@ManyToMany(mappedBy = "productos", cascade = CascadeType.ALL)
@JsonIgnore
private List<CarritoCompras> carritos = new ArrayList<>();

    public Producto() {
    }

    public Producto(Integer idProducto, String nombre, String descripcion, float precio, int talla, String color, String genero, byte[] imagen, String tipoZapato, Inventario inventario) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.talla = talla;
        this.color = color;
        this.genero = genero;
        this.imagen = imagen;
        this.tipoZapato = tipoZapato;
        this.inventario = inventario;
    }

    // getters y setters

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

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getTipoZapato() {
        return tipoZapato;
    }

    public void setTipoZapato(String tipoZapato) {
        this.tipoZapato = tipoZapato;
    }

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }
    public List<CarritoCompras> getCarritos() {
        return carritos;
    }
   public void setCarritos(List<CarritoCompras> carritos) {
    this.carritos = carritos;
}
}
