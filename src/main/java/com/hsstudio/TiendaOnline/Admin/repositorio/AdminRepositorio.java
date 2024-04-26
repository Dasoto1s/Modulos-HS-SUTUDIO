package com.hsstudio.TiendaOnline.Admin.repositorio;

import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepositorio extends JpaRepository<Admin, Integer> {
    Admin findByEmail(String email);
}
