/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.Cliente.entidad;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "devolucion_cambio")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SolicitudCambioDevolucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "N_solicitud")
    private Integer N_solicitud;

    @Temporal(TemporalType.DATE)
    private Date Fecha_solicitud = new Date();

    @JsonProperty("Estado_solicitud")
    private String Estado_solicitud;
    @JsonProperty("Motivo_solicitud")
    private String Motivo_solicitud;
    @JsonProperty("Producto_relacionado")
    private String Producto_relacionado;
    @JsonProperty("Mensaje_recivido_cliente")
    private String Mensaje_recivido_cliente;

    public SolicitudCambioDevolucion( String Estado_solicitud, String Motivo_solicitud, String Producto_relacionado, String Mensaje_recivido_cliente) {
  
        this.Estado_solicitud = Estado_solicitud;
        this.Motivo_solicitud = Motivo_solicitud;
        this.Producto_relacionado = Producto_relacionado;
        this.Mensaje_recivido_cliente = Mensaje_recivido_cliente;
    }
    
    
     public SolicitudCambioDevolucion() {
       
    }
    @JsonProperty("N_solicitud")
    public Integer getN_solicitud() {
        return N_solicitud;
    }

    public void setN_solicitud(Integer N_solicitud) {
        this.N_solicitud = N_solicitud;
    }

    @JsonProperty("Fecha_solicitud")
    public Date getFecha_solicitud() {
        return Fecha_solicitud;
    }

    public void setFecha_solicitud(Date Fecha_solicitud) {
        this.Fecha_solicitud = Fecha_solicitud;
    }
    
    @JsonProperty("Estado_solicitud")
    public String getEstado_solicitud() {
        return Estado_solicitud;
    }

    public void setEstado_solicitud(String Estado_solicitud) {
        this.Estado_solicitud = Estado_solicitud;
    }
    @JsonProperty("Motivo_solicitud")
    public String getMotivo_solicitud() {
        return Motivo_solicitud;
    }

    public void setMotivo_solicitud(String Motivo_solicitud) {
        this.Motivo_solicitud = Motivo_solicitud;
    }
    @JsonProperty("Producto_relacionado")
    public String getProducto_relacionado() {
        return Producto_relacionado;
    }

    public void setProducto_relacionado(String Producto_relacionado) {
        this.Producto_relacionado = Producto_relacionado;
    }
    @JsonProperty("Mensaje_recivido_cliente")
    public String getMensaje_recivido_cliente() {
        return Mensaje_recivido_cliente;
    }

    public void setMensaje_recivido_cliente(String Mensaje_recivido_cliente) {
        this.Mensaje_recivido_cliente = Mensaje_recivido_cliente;
    }

 
    
}