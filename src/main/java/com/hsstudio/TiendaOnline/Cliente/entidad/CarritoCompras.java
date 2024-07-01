package com.hsstudio.TiendaOnline.Cliente.entidad;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;



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
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "carrito_producto", 
    joinColumns = @JoinColumn(name = "id_carrito"), 
    inverseJoinColumns = @JoinColumn(name = "id_producto")
    )
    
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Producto> productos = new ArrayList<>();

    
       @OneToMany(mappedBy = "carritoCompras")
    private List<Pedido> pedidos = new ArrayList<>();
    

    
    @Column(name = "session_id")
    private String sessionId;
    
   
     @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;
    
    
    public CarritoCompras() {
    }

    public CarritoCompras(Integer idCarrito, Integer numeroProductos, Float precioTotal, String sessionId, Date fechaCreacion) {
        this.idCarrito = idCarrito;
        this.numeroProductos = numeroProductos;
        this.precioTotal = precioTotal;
        this.sessionId = sessionId;
        this.fechaCreacion = fechaCreacion;
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

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
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

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

  

   

   
   
}