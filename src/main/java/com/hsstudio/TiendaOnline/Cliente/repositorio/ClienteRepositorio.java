package com.hsstudio.TiendaOnline.Cliente.repositorio;

import com.hsstudio.TiendaOnline.Cliente.entidad.CarritoCompras;
import com.hsstudio.TiendaOnline.Cliente.entidad.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, String> {
 

    @Query("SELECT c FROM Cliente c JOIN CarritoCompras cc ON c.idCliente = cc.sessionId WHERE cc.sessionId = :sessionId")
    Cliente findBySessionId(@Param("sessionId") String sessionId);

   
}