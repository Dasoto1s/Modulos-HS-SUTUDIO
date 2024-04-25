/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.Admin.repositorio;

import com.hsstudio.TiendaOnline.Admin.entidad.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepositorio extends JpaRepository<Inventario, Integer > {
}