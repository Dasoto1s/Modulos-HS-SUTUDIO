package com.hsstudio.TiendaOnline.Admin.entidad;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.Objects;



  
@Entity
public class Producto implements Serializable  {
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
    
    @OneToOne(mappedBy = "producto", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Inventario inventario;
    
@ManyToMany(mappedBy = "productos")
private List<CarritoCompras> carritos = new ArrayList<>();

   

    public Producto() {
    }

    public Producto(Integer idProducto, String nombre, String descripcion, float precio, int talla, String color, String genero, Inventario inventario) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.talla = talla;
        this.color = color;
        this.genero = genero;
        this.inventario = inventario;
    }


    @Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Producto producto = (Producto) o;
    return idProducto.equals(producto.idProducto);
}

@Override
public int hashCode() {
    return Objects.hash(idProducto);
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

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

  
    
    
    
}