package com.hsstudio.TiendaOnline.Cliente.repositorio;

import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Cliente.entidad.Promociones;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromocionesRepositorio extends JpaRepository<Promociones, Integer> {
    Optional<Promociones> findByProductoIdProducto(Integer productoId);
    void deleteByProducto(Producto producto);
}