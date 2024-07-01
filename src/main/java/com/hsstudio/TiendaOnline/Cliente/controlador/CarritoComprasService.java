/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.Cliente.controlador;


import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.repositorio.CarritoComprasRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoComprasService {

    @Autowired
    private CarritoComprasRepositorio carritoComprasRepositorio;
@Transactional
public void limpiarCarrito(String sessionId) {
    CarritoCompras carrito = carritoComprasRepositorio.findBySessionId(sessionId);
    if (carrito != null) {
        if (carrito.getProductos() != null) {
            carrito.getProductos().clear();
        }
        carrito.setNumeroProductos(0);
        carrito.setPrecioTotal(0.0f);
        carritoComprasRepositorio.save(carrito);
    } else {
        throw new RuntimeException("Carrito no encontrado para la sesión proporcionada");
    }
}
    
}