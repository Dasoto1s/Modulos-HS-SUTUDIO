package com.hsstudio.TiendaOnline.Admin.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

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
    
    @OneToOne
    @JoinColumn(name = "Id_inventario7")
    private Inventario inventario;
        
    public Admin() {
        }

    public Admin(Integer id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.inventario = inventario;
    }

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

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    
}
