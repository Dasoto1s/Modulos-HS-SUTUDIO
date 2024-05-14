package com.hsstudio.TiendaOnline.Cliente.repositorio;

import com.hsstudio.TiendaOnline.Cliente.entidad.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, String> {
    Cliente findByIdCliente(String idCliente);
}
