package com.hsstudio.TiendaOnline.Cliente.entidad;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "N_pedido")
    private Integer numeroPedido;

    @Column(name = "Direccion_envio")
    private String direccionEnvio;

    @Column(name = "Fecha_pedido")
    private Date fechaPedido;

    @ManyToOne
    @JoinColumn(name = "Id_carrito")
    private CarritoCompras carritoCompras;

    @ManyToOne
    @JoinColumn(name = "Id_administrador")
    private Admin admin;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "ciudad")
    private String ciudad;  
    
     @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
private Transaccion transaccion;
    
 
    @Column(name = "Estado_pedido")
private String estadoPedido;
    
    private Double totalConDescuento;
    
    private Double precioTotal; 
    

    // Constructor, getters y setters

    public Pedido(Integer numeroPedido, String direccionEnvio, Date fechaPedido, CarritoCompras carritoCompras, Admin admin, String departamento, String ciudad, Transaccion transaccion, String estadoPedido, Double totalConDescuento, Double precioTotal) {
        this.numeroPedido = numeroPedido;
        this.direccionEnvio = direccionEnvio;
        this.fechaPedido = fechaPedido;
        this.carritoCompras = carritoCompras;
        this.admin = admin;
        this.departamento = departamento;
        this.ciudad = ciudad;
        this.transaccion = transaccion;
        this.estadoPedido = estadoPedido;
        this.totalConDescuento = totalConDescuento;
        this.precioTotal = precioTotal;
    }


 
        public Pedido() {

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

    public CarritoCompras getCarritoCompras() {
        return carritoCompras;
    }

    public void setCarritoCompras(CarritoCompras carritoCompras) {
        this.carritoCompras = carritoCompras;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
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


    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }
    
public String getEstadoPedido() {
    return estadoPedido;
}

public void setEstadoPedido(String estadoPedido) {
    this.estadoPedido = estadoPedido;
}

    public Double getTotalConDescuento() {
        return totalConDescuento;
    }

    public void setTotalConDescuento(Double totalConDescuento) {
        this.totalConDescuento = totalConDescuento;
    }

    public Double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(Double precioTotal) {
        this.precioTotal = precioTotal;
    }


    
}