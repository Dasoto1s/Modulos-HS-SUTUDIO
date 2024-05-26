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
    @Column(name = "Estado_solicitud_bool")
    private String Estado_solicitud;

    @JsonProperty("Motivo_solicitud")
    private String Motivo_solicitud;

    @JsonProperty("Producto_relacionado")
    private String Producto_relacionado;

    @JsonProperty("Mensaje_recivido_cliente")
    private String Mensaje_recivido_cliente;

    @JsonProperty("Correo_Cliente")
    private String correo_cliente;
    
    @JsonProperty("Tipo_solicitud")
    @Column(name = "Tipo_solicitud")
    private String tipo_solicitud;
    
        @JsonProperty("nombreCliente")
    private String nombreCliente;

    @JsonProperty("numeroContactoCliente")
    private String numeroContactoCliente;

    // Constructor completo

    public SolicitudCambioDevolucion(Integer N_solicitud, String Estado_solicitud, String Motivo_solicitud, String Producto_relacionado, String Mensaje_recivido_cliente, String correo_cliente, String tipo_solicitud, String nombreCliente, String numeroContactoCliente) {
        this.N_solicitud = N_solicitud;
        this.Estado_solicitud = Estado_solicitud;
        this.Motivo_solicitud = Motivo_solicitud;
        this.Producto_relacionado = Producto_relacionado;
        this.Mensaje_recivido_cliente = Mensaje_recivido_cliente;
        this.correo_cliente = correo_cliente;
        this.tipo_solicitud = tipo_solicitud;
        this.nombreCliente = nombreCliente;
        this.numeroContactoCliente = numeroContactoCliente;
    }
   

    // Constructor vacío
    public SolicitudCambioDevolucion() {}

    // Getters y setters
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

    @JsonProperty("Correo_Cliente")
    public String getCorreo_cliente() {
        return correo_cliente;
    }

    public void setCorreo_cliente(String correo_cliente) {
        this.correo_cliente = correo_cliente;
    }

    @JsonProperty("Tipo_solicitud")
    public String getTipo_solicitud() {
        return tipo_solicitud;
    }

    public void setTipo_solicitud(String tipo_solicitud) {
        this.tipo_solicitud = tipo_solicitud;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNumeroContactoCliente() {
        return numeroContactoCliente;
    }

    public void setNumeroContactoCliente(String numeroContactoCliente) {
        this.numeroContactoCliente = numeroContactoCliente;
    }
    
}
