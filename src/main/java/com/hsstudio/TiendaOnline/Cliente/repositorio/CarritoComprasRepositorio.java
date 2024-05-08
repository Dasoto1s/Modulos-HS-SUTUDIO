package com.hsstudio.TiendaOnline.Cliente.repositorio;

import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoComprasRepositorio extends JpaRepository<CarritoCompras, Integer> {
    CarritoCompras findBySessionId(String sessionId);
    Optional<CarritoCompras> findById(Integer id);
}


