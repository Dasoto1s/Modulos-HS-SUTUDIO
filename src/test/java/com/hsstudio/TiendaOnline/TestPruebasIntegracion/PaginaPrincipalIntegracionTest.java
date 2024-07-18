/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hsstudio.TiendaOnline.TestPruebasIntegracion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Cliente.entidad.Destacados;
import com.hsstudio.TiendaOnline.Cliente.entidad.Promociones;
import com.hsstudio.TiendaOnline.Cliente.repositorio.DestacadosRepositorio;
import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class PaginaPrincipalIntegracionTest {

    @Autowired
    private MockMvc mockMvc;
    
    
    
     @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testObtenerProductosPorGenero() throws Exception {
        MvcResult result = mockMvc.perform(get("/pagina-principal/productos/{genero}", "hombres"))
                                  .andExpect(status().isOk())
                                  .andReturn();

        String content = result.getResponse().getContentAsString();
        // Aquí puedes realizar más validaciones sobre el contenido de la respuesta si es necesario
        assertThat(content).isNotNull();  // Verifica que la respuesta no sea nula
    }

    @Test
    public void testObtenerProductosPorSubcategoria() throws Exception {
        MvcResult result = mockMvc.perform(get("/pagina-principal/productos/{genero}/{tipoZapato}", "mujeres", "botas"))
                                  .andExpect(status().isOk())
                                  .andReturn();

        String content = result.getResponse().getContentAsString();
        // Puedes agregar más validaciones según la estructura esperada de la respuesta JSON
        assertThat(content).isNotNull();
    }
    
  

}
