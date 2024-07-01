package com.hsstudio.TiendaOnline.Cliente.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigInteger;

@Entity
@Table(name = "cliente")
public class Cliente {
    @Id
    @Column(name = "id_cliente")
    private String idCliente;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "Correo")
    private String correo;
    @Column(name = "Telefono")
    private BigInteger telefono;

    public Cliente() {
        // Constructor vacío requerido por JPA
    }

    public Cliente(String idCliente, String nombre, String correo, BigInteger telefono) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
    }

    // Getters y setters
    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public BigInteger getTelefono() {
        return telefono;
    }

    public void setTelefono(BigInteger telefono) {
        this.telefono = telefono;
    }
}