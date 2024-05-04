/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.Cliente.entidad;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;



@Entity
@Table(name = "carrito_compras")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarritoCompras implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_carrito")
    private Integer idCarrito;
    
    @Column(name = "Numero_productos")
    private Integer numeroProductos;
    
    @Column(name = "precio_total")
    private Float precioTotal;
     
    // Esta es la relación @ManyToMany con Producto. Un carrito de compras puede tener muchos productos.
    @ManyToMany
@JoinTable(
    name = "carrito_producto", 
    joinColumns = @JoinColumn(name = "id_carrito"), 
    inverseJoinColumns = @JoinColumn(name = "id_producto")
)
private List<Producto> productos = new ArrayList<>();


    
    @Column(name = "session_id")
    private String sessionId;
    
    
    public CarritoCompras() {
    }

    public CarritoCompras(Integer idCarrito, Integer numeroProductos, Float precioTotal) {
        this.idCarrito = idCarrito;
        this.numeroProductos = numeroProductos;
        this.precioTotal = precioTotal;
    }
    
     public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    
public List<Producto> getProductos() {
    return productos;
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

   
   
}
