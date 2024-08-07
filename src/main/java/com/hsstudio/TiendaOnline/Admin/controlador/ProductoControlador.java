    package com.hsstudio.TiendaOnline.Admin.controlador;

    import com.auth0.jwt.JWT;
    import com.auth0.jwt.exceptions.JWTDecodeException;
    import com.auth0.jwt.interfaces.DecodedJWT;
    import com.hsstudio.TiendaOnline.Admin.entidad.*;
    import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
    import com.hsstudio.TiendaOnline.Admin.repositorio.InventarioRepositorio;
    import com.hsstudio.TiendaOnline.Admin.repositorio.ProductoRepositorio;
    import com.hsstudio.TiendaOnline.Cliente.repositorio.PromocionesRepositorio;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.SignatureAlgorithm;
    import io.jsonwebtoken.security.Keys;
    import java.io.IOException;
    import java.util.ArrayList;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import javax.crypto.SecretKey;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.Optional;
    import java.util.logging.Level;
    import java.util.logging.Logger;
    import java.util.stream.Collectors;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import com.hsstudio.TiendaOnline.Admin.entidad.MensajeRespuesta;

    @RestController
    @RequestMapping("/productos")
    @CrossOrigin(origins = "http://localhost:3000")
    public class ProductoControlador {

        private final ProductoRepositorio productoRepositorio;
        private final InventarioRepositorio inventarioRepositorio;
        private final AdminRepositorio adminRepositorio;

        private final SecretKey secretKey;


        private final PromocionesRepositorio promocionesRepositorio;
      private final ProductoService productoService;



        @Autowired
        public ProductoControlador(ProductoRepositorio productoRepositorio,
                                   InventarioRepositorio inventarioRepositorio,
                                   AdminRepositorio adminRepositorio,
                                   PromocionesRepositorio promocionesRepositorio,
                                   ProductoService  productoService) {
            this.productoRepositorio = productoRepositorio;
            this.inventarioRepositorio = inventarioRepositorio;
            this.adminRepositorio = adminRepositorio;
            this.promocionesRepositorio = promocionesRepositorio;
            this.productoService = productoService;
            this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }

        public  Integer extraerAdminIdDesdeToken(String authorizationHeader) {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                DecodedJWT decodedJWT = JWT.decode(token);
                return decodedJWT.getClaim("adminId").asInt();
            } else {
                throw new JWTDecodeException("Token JWT inválido");
            }
        }


        // Obtener todos los productos
    @GetMapping
    public List<ProductoDTO> obtenerTodos() {
        List<Producto> productos = productoRepositorio.findAll();

        List<ProductoDTO> productoDTOs = productos.stream()
            .map(producto -> {
                ProductoDTO dto = new ProductoDTO();
                dto.setIdProducto(producto.getIdProducto());
                dto.setNombre(producto.getNombre());
                dto.setDescripcion(producto.getDescripcion());
                dto.setPrecio(producto.getPrecio());
                dto.setTalla(producto.getTalla());
                dto.setColor(producto.getColor());
                dto.setGenero(producto.getGenero());
                dto.setTipoZapato(producto.getTipoZapato());
                dto.setImagen(producto.getImagen());

                // Obtener el objeto Inventario asociado al producto
                Inventario inventario = producto.getInventario();
                if (inventario != null) {
                    dto.setCantidad(inventario.getCantidad());
                    dto.setStock(inventario.getStock());
                    dto.setCantidadMinimaRequerida(inventario.getCantidad_minima_requerida());
                }

                return dto;
            })
            .collect(Collectors.toList());

        return productoDTOs;
    }

 
        //crear producto
    @PostMapping
public ResponseEntity<Object> crearProducto(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestParam("nombre") String nombre,
        @RequestParam("descripcion") String descripcion,
        @RequestParam("precio") float precio,
        @RequestParam("talla") String tallaStr,
        @RequestParam("color") String color,
        @RequestParam("genero") String genero,
        @RequestParam("tipoZapato") String tipoZapato,
        @RequestParam("imagen") MultipartFile imagen,
        @RequestParam("cantidad") int cantidad,
        @RequestParam("stock") int stock,
        @RequestParam("cantidadMinimaRequerida") int cantidadMinimaRequerida
) throws IOException {
    // Verificar la autenticación
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("No autorizado"));
    }

    // Validar que todos los campos requeridos estén presentes
    if (nombre == null || nombre.isEmpty() ||
        descripcion == null || descripcion.isEmpty() ||
        tallaStr == null || tallaStr.isEmpty() ||
        color == null || color.isEmpty() ||
        genero == null || genero.isEmpty() ||
        tipoZapato == null || tipoZapato.isEmpty() ||
        imagen == null || imagen.isEmpty() ||
        cantidad < 0 || stock < 0 || cantidadMinimaRequerida < 0) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Todos los campos requeridos deben estar presentes y ser válidos."));
    }

    // Obtener el ID del administrador desde el token JWT
    Integer adminId;
    try {
        adminId = extraerAdminIdDesdeToken(authorizationHeader);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Token inválido"));
    }

    // Buscar al administrador por su ID
    Admin admin = adminRepositorio.findById(adminId)
            .orElse(null);

    if (admin == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Administrador no encontrado"));
    }

    // Validar la longitud de la talla
    if (tallaStr.length() != 2) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("La talla debe tener exactamente dos dígitos."));
    }

    // Convertir la talla a un entero
    int talla;
    try {
        talla = Integer.parseInt(tallaStr);
        if (talla <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("La talla debe ser un número positivo."));
        }
    } catch (NumberFormatException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("La talla no es un número válido."));
    }

    Producto producto = new Producto();
    producto.setNombre(nombre);
    producto.setDescripcion(descripcion);
    producto.setPrecio(precio);
    producto.setTalla(talla);
    producto.setColor(color);
    producto.setGenero(genero);
    producto.setTipoZapato(tipoZapato);
    producto.setImagen(imagen.getBytes());

    Inventario inventario = new Inventario();
    inventario.setAdmin(admin);
    inventario.setCantidad(cantidad);
    inventario.setStock(stock);
    inventario.setCantidad_minima_requerida(cantidadMinimaRequerida);
    producto.setInventario(inventario);
    inventario.setProducto(producto);

    try {
        Producto productoGuardado = productoRepositorio.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error al guardar el producto: " + e.getMessage()));
    }
}
        // Actualizar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
        @PathVariable Integer id,
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String descripcion,
        @RequestParam(required = false) String color,
        @RequestParam(required = false) Float precio,
        @RequestParam(required = false) Integer talla,
        @RequestParam(required = false) String genero,
        @RequestParam(required = false) String tipoZapato,
        @RequestParam(required = false) MultipartFile imagen,
        @RequestParam(required = false) Integer cantidad,
        @RequestParam(required = false) Integer stock,
        @RequestParam(required = false) Integer cantidadMinimaRequerida
    ) throws IOException {

        return (ResponseEntity<Producto>) productoRepositorio.findById(id)
            .map(producto -> {
                List<String> camposVacios = new ArrayList<>();

                if (nombre != null && !nombre.isEmpty()) {
                    producto.setNombre(nombre);
                }

                if (descripcion != null && !descripcion.isEmpty()) {
                    producto.setDescripcion(descripcion);
                }

                if (color != null && !color.isEmpty()) {
                    producto.setColor(color);
                }

                if (precio != null) {
                    producto.setPrecio(precio);
                }

                if (talla != null) {
                    producto.setTalla(talla);
                }

                if (genero != null && !genero.isEmpty()) {
                    producto.setGenero(genero);
                }

                if (tipoZapato != null && !tipoZapato.isEmpty()) {
                    producto.setTipoZapato(tipoZapato);
                }

                if (imagen != null && !imagen.isEmpty()) {
                    try {
                        producto.setImagen(imagen.getBytes());
                    } catch (IOException ex) {
                        Logger.getLogger(ProductoControlador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (cantidad != null) {
                    producto.getInventario().setCantidad(cantidad);
                }

                if (stock != null) {
                    producto.getInventario().setStock(stock);
                }

                if (cantidadMinimaRequerida != null) {
                    producto.getInventario().setCantidad_minima_requerida(cantidadMinimaRequerida);
                }

                if (!camposVacios.isEmpty()) {
                    String mensajeError = "Los siguientes campos no pueden estar vacíos: \n" + String.join("\n", camposVacios);
                    return ResponseEntity.badRequest().body(new MensajeRespuesta(mensajeError));
                }

                Producto productoActualizadoGuardado = productoRepositorio.save(producto);
                return ResponseEntity.ok().body(productoActualizadoGuardado);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

       // Eliminar un producto
        @DeleteMapping("/{id}")
       public ResponseEntity<?> eliminarProducto(@PathVariable Integer id) {
           productoService.eliminarProducto(id);
           return ResponseEntity.ok("Producto eliminado con éxito");
       }

      

      @GetMapping("/buscar")
    public ResponseEntity<Object> buscarProductos(
        @RequestParam(required = false) String palabrasClave,
        @RequestParam(required = false) Integer talla,
        @RequestParam(required = false) Integer idProducto
    ) {
        List<Producto> productos;
        if (idProducto != null) {
            Optional<Producto> productoOptional = productoRepositorio.findById(idProducto);
            if (productoOptional.isPresent()) {
                productos = List.of(productoOptional.get());
            } else {
                productos = List.of(); // Lista vacía si no se encuentra el producto
            }
        } else if (palabrasClave != null && talla != null) {
            productos = productoRepositorio.findByNombreContainingIgnoreCaseAndTalla(palabrasClave, talla);
        } else if (palabrasClave != null) {
            productos = productoRepositorio.buscarPorNombreIgnoreCaseNativo(palabrasClave); // Cambiar aquí
        } else if (talla != null) {
            productos = productoRepositorio.findByTalla(talla);
        } else {
            productos = productoRepositorio.findAll();
        }

            if (productos.isEmpty()) {
                return ResponseEntity.ok(new MensajeRespuesta("No se encontraron productos para la búsqueda especificada."));
            }

           List<ProductoDTO> productoDTOs = productos.stream()
        .map(producto -> {
            ProductoDTO dto = new ProductoDTO();
            dto.setIdProducto(producto.getIdProducto());
            dto.setNombre(producto.getNombre());
            dto.setDescripcion(producto.getDescripcion());
            dto.setPrecio(producto.getPrecio());
            dto.setTalla(producto.getTalla());
            dto.setColor(producto.getColor());
            dto.setGenero(producto.getGenero());
            dto.setTipoZapato(producto.getTipoZapato());
            dto.setImagen(producto.getImagen());

            // Obtener el objeto Inventario asociado al producto
            Inventario inventario = producto.getInventario();
            if (inventario != null) {
                dto.setCantidad(inventario.getCantidad());
                dto.setStock(inventario.getStock());
                dto.setCantidadMinimaRequerida(inventario.getCantidad_minima_requerida());
            }

            return dto;
        })
        .collect(Collectors.toList());

            return ResponseEntity.ok(productoDTOs);
        }
    }

  

    class ErrorResponse {
        private String mensaje;

        public ErrorResponse(String mensaje) {
            this.mensaje = mensaje;
        }

        public String getMensaje() {
            return mensaje;
        }
    }