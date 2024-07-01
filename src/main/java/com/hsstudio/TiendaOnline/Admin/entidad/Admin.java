package com.hsstudio.TiendaOnline.Admin.entidad;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hsstudio.TiendaOnline.Cliente.entidad.Pedido;
import jakarta.persistence.*;
import java.util.List;

@Entity(name = "administrador")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_administrador")
    private Integer id;

    @Column(name = "Nombre_usuario", unique = true)
    private String email;

    @Column(name = "contraseña")
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "admin")
    private List<Inventario> inventarios;

    @JsonIgnore
    @OneToMany(mappedBy = "admin")
    private List<Pedido> pedidos;

   @OneToMany(mappedBy = "admin")
private List<Banner> banners;

    public Admin(Integer id, String email, String password, List<Inventario> inventarios, List<Pedido> pedidos, List<Banner> banners) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.inventarios = inventarios;
        this.pedidos = pedidos;
        this.banners = banners;
    }

    // Constructor sin parámetros
    public Admin() {
    }

   

    

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Inventario> getInventarios() {
        return inventarios;
    }

    public void setInventarios(List<Inventario> inventarios) {
        this.inventarios = inventarios;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

   
    
}
