
package com.hsstudio.TiendaOnline.Admin.repositorio;

import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {
    List<Producto> findByGenero(String genero);
    List<Producto> findByNombreContainingIgnoreCaseAndTalla(String palabrasClave, int talla);
    List<Producto> findByNombreContainingIgnoreCase(String palabrasClave);
    List<Producto> findByTalla(int talla);
}
