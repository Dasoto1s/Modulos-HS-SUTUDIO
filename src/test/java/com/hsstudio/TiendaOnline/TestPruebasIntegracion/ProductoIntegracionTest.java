package com.hsstudio.TiendaOnline.TestPruebasIntegracion;

import com.hsstudio.TiendaOnline.Admin.controlador.ProductoControlador;
import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import com.hsstudio.TiendaOnline.Admin.entidad.Producto;
import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductoIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @MockBean
    private AdminRepositorio adminRepositorio;

    private SecretKey secretKey;

    @BeforeEach
    public void setUp() {
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    @Test
    public void testCrearProducto() throws Exception {
        // Configurar el mock de adminRepositorio
        Admin admin = new Admin();
        admin.setId(1);
        admin.setEmail("admin@test.com");
        when(adminRepositorio.findById(1)).thenReturn(Optional.of(admin));

        // Generar el token JWT
        String token = Jwts.builder()
            .setSubject(admin.getEmail())
            .claim("adminId", admin.getId())
            .signWith(secretKey)
            .compact();

        // Crear el archivo simulado
        MockMultipartFile imagen = new MockMultipartFile("imagen", "zapato.jpg", "image/jpeg", "Zapato imagen".getBytes());

        // Realizar la solicitud y verificar la respuesta
        mockMvc.perform(multipart("/productos")
                .file(imagen)
                .param("nombre", "Zapato Casual")
                .param("descripcion", "Zapato elegante para ocasiones casuales")
                .param("precio", "100.0")
                .param("talla", "40")
                .param("color", "Marrón")
                .param("genero", "Hombre")
                .param("tipoZapato", "Casual")
                .param("cantidad", "50")
                .param("stock", "100")
                .param("cantidadMinimaRequerida", "10")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Zapato Casual"));

        // Verificar que el método findById fue llamado
        verify(adminRepositorio).findById(1);
    }

    @Test
    public void testObtenerTodosLosProductos() throws Exception {
        mockMvc.perform(get("/productos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].nombre").exists())
                .andExpect(jsonPath("$[*].nombre", hasItem("Mocasines de Cuero")))
                .andExpect(jsonPath("$[*].nombre", hasItem("Botas Chelsea de Cuero")))
                .andExpect(jsonPath("$[*].genero", hasItems("Hombre", "Mujer", "Niño")));
    }

    @Test
    public void testEliminarProducto() throws Exception {
        // Crear un producto de prueba
        Producto producto = new Producto();
        producto.setNombre("Zapato a Eliminar");
        producto.setDescripcion("Descripción del zapato a eliminar");
        producto.setPrecio(80.0f);
        producto.setTalla(41);
        producto.setColor("Azul");
        producto.setGenero("Mujer");
        producto.setTipoZapato("Casual");
        producto = productoRepositorio.save(producto);

        mockMvc.perform(delete("/productos/" + producto.getIdProducto())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Producto eliminado con éxito"));

        Optional<Producto> productoEliminado = productoRepositorio.findById(producto.getIdProducto());
        assert(productoEliminado.isEmpty());
    }
}