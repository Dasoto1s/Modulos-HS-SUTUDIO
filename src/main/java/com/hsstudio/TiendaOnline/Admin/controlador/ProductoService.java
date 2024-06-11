/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.Admin.controlador;


import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.Cliente;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.ClienteRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PedidoRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {
    private final ProductoRepositorio productoRepositorio;
    private final PromocionesRepositorio promocionesRepositorio;
    private final PedidoRepositorio pedidoRepositorio;
    private final CarritoComprasRepositorio carritoComprasRepositorio;
    private final ClienteRepositorio clienteRepositorio;

    @Autowired
    public ProductoService(ProductoRepositorio productoRepositorio,
                           PromocionesRepositorio promocionesRepositorio,
                           PedidoRepositorio pedidoRepositorio,
                           CarritoComprasRepositorio carritoComprasRepositorio,
                           ClienteRepositorio clienteRepositorio) {
        this.productoRepositorio = productoRepositorio;
        this.promocionesRepositorio = promocionesRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
        this.carritoComprasRepositorio = carritoComprasRepositorio;
        this.clienteRepositorio= clienteRepositorio;
    }

 @Transactional
public void eliminarProducto(Integer id) {
    productoRepositorio.findById(id)
        .ifPresent(producto -> {
            // Eliminar las promociones asociadas al producto
            promocionesRepositorio.deleteByProducto(producto);
            
            // Obtener los carritos de compras asociados al producto
            List<CarritoCompras> carritos = producto.getCarritos();
            
            for (CarritoCompras carrito : carritos) {
                // Eliminar los pedidos asociados a cada carrito de compras
                pedidoRepositorio.deleteByCarritoCompras(carrito);
                
                // Eliminar el producto del carrito de compras
                carrito.getProductos().remove(producto);
                
                // Guardar los cambios en el carrito de compras
                carritoComprasRepositorio.save(carrito);
            }
            
            // Eliminar el producto
            productoRepositorio.delete(producto);
        });
}

}