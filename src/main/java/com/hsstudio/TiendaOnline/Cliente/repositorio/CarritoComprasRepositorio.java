package com.hsstudio.TiendaOnline.Cliente.repositorio;

import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoComprasRepositorio extends JpaRepository<CarritoCompras, Integer> {
    CarritoCompras findBySessionId(String sessionId);
    Optional<CarritoCompras> findById(Integer id);

    @Query("SELECT c FROM CarritoCompras c WHERE c.fechaCreacion < :fechaLimite AND c.pedidos IS EMPTY")
    List<CarritoCompras> findByFechaCreacionBeforeAndPedidosIsEmpty(@Param("fechaLimite") Date fechaLimite);

    @Modifying
    @Query("DELETE FROM CarritoCompras c WHERE c.idCarrito = :carritoId")
    void deleteCarritoAndProductosById(@Param("carritoId") Integer carritoId);
}