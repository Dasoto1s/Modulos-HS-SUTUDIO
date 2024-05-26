
package com.hsstudio.TiendaOnline.Cliente.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "transaccion")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_transaccion")
    private Integer idTransaccion;
    

    @OneToOne
    @PrimaryKeyJoinColumn(name = "id_pedido")
    private Pedido pedido;

    @Column(name = "Monto_total")
    private Double montoTotal;

    @Column(name = "Metodo_pago")
    private String metodoPago;
    
    @Column(name = "estado")
    private String Estado;

    public Transaccion(Integer idTransaccion, Pedido pedido, Double montoTotal, String metodoPago, String Estado) {
        this.idTransaccion = idTransaccion;
        this.pedido = pedido;
        this.montoTotal = montoTotal;
        this.metodoPago = metodoPago;
        this.Estado = Estado;
    }

    

    
    public Transaccion() {
    
    }

    public Integer getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(Integer idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

 

    public Double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return Estado;
    }

   public void setEstado(String estado) {
    this.Estado = estado; // Asignar el valor directamente a la propiedad Estado
}
    }

    
