package com.hsstudio.TiendaOnline.Admin.repositorio;

import com.hsstudio.TiendaOnline.Admin.entidad.Banner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepositorio extends JpaRepository<Banner, Integer> {
      Optional<Banner> findByPosicion(Integer posicion);
}
