package com.hsstudio.TiendaOnline.Admin.dto;

import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import java.math.BigInteger;

import java.util.Date;
import java.util.List;

public class PedidoDTO {
    private Integer numeroPedido;
    private String direccionEnvio;
    private Date fechaPedido;
    private String departamento;
    private String ciudad;
    private Integer numeroProductos;
    private Float precioTotal;
    private List<Producto> productos;
    private String nombreCliente;
    private String correoCliente;
    private BigInteger telefonoCliente;
    private String metodoPagoCliente;
    private String Estado_solicitud;

    public PedidoDTO(Integer numeroPedido, String direccionEnvio, Date fechaPedido, String departamento, String ciudad, Integer numeroProductos, Float precioTotal, List<Producto> productos, String nombreCliente, String correoCliente, BigInteger telefonoCliente, String metodoPagoCliente, String Estado_solicitud) {
        this.numeroPedido = numeroPedido;
        this.direccionEnvio = direccionEnvio;
        this.fechaPedido = fechaPedido;
        this.departamento = departamento;
        this.ciudad = ciudad;
        this.numeroProductos = numeroProductos;
        this.precioTotal = precioTotal;
        this.productos = productos;
        this.nombreCliente = nombreCliente;
        this.correoCliente = correoCliente;
        this.telefonoCliente = telefonoCliente;
        this.metodoPagoCliente = metodoPagoCliente;
        this.Estado_solicitud = Estado_solicitud;
    }

 
      public PedidoDTO() {
        
    }

    public Integer getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(Integer numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
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

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public BigInteger getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(BigInteger telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }



    public String getMetodoPagoCliente() {
        return metodoPagoCliente;
    }

    public void setMetodoPagoCliente(String metodoPagoCliente) {
        this.metodoPagoCliente = metodoPagoCliente;
    }

    public String getEstado_solicitud() {
        return Estado_solicitud;
    }

    public void setEstado_solicitud(String Estado_solicitud) {
        this.Estado_solicitud = Estado_solicitud;
    }

    
}