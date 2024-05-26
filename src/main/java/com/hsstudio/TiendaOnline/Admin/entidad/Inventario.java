package com.hsstudio.TiendaOnline.Admin.entidad;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_inventario1")
    private Integer idInventario;

    private Float cantidad;

    private Integer stock;

    private Integer Cantidad_minima_requerida;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "Id_administrador1")
    private Admin admin;

    @OneToOne
    @JoinColumn(name = "Id_Producto5")
    @JsonBackReference
    private Producto producto;

    public Inventario() {
    }

    public Integer getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public void setCantidad(Integer nuevaCantidad) {
        this.cantidad = (float) nuevaCantidad;
    }
}