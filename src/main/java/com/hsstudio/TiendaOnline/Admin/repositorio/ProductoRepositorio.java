
package com.hsstudio.TiendaOnline.Admin.repositorio;

import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {
    List<Producto> findByGenero(String genero);
    List<Producto> findByNombreContainingIgnoreCaseAndTalla(String palabrasClave, int talla);
    List<Producto> findByNombreContainingIgnoreCase(String palabrasClave);
    List<Producto> findByTalla(int talla);
     List<Producto> findByGeneroAndTipoZapato(String genero, String tipoZapato);
     @Query(value = "SELECT * FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombreParcial, '%'))", nativeQuery = true)
List<Producto> buscarPorNombreIgnoreCaseNativo(@Param("nombreParcial") String nombreParcial);
}
