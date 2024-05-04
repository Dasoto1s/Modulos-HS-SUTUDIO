/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.Cliente.repositorio;

import com.hsstudio.TiendaOnline.Cliente.entidad.Promociones;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromocionesRepositorio extends JpaRepository<Promociones, Integer> {
    // Puedes agregar métodos personalizados aquí si es necesario
}

