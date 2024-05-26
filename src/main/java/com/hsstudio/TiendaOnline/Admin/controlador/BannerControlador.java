package com.hsstudio.TiendaOnline.Admin.controlador;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import com.hsstudio.TiendaOnline.Admin.entidad.Banner;
import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.BannerRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/banner")
public class BannerControlador {

    private final BannerRepositorio bannerRepositorio;
    private final AdminRepositorio adminRepositorio;

    @Autowired
    public BannerControlador(BannerRepositorio bannerRepositorio, AdminRepositorio adminRepositorio) {
        this.bannerRepositorio = bannerRepositorio;
        this.adminRepositorio = adminRepositorio;
    }

    // Subir nuevas imágenes al banner
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImages(@RequestHeader("Authorization") String authorizationHeader,
                                          @RequestParam("imagen1") MultipartFile imagen1,
                                          @RequestParam("imagen2") MultipartFile imagen2,
                                          @RequestParam("imagen3") MultipartFile imagen3) throws Exception {
        // Verificar la presencia del token JWT
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            // Obtener el ID del administrador desde el token JWT
            Integer adminId = extraerAdminIdDesdeToken(authorizationHeader);

            // Buscar al administrador por su ID
            Optional<Admin> adminOptional = adminRepositorio.findById(adminId);

            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();

                Banner banner = new Banner();
                banner.setImagen1(imagen1.getBytes());
                banner.setImagen2(imagen2.getBytes());
                banner.setImagen3(imagen3.getBytes());
                banner.setAdmin(admin); // Asociar el banner con el administrador

                bannerRepositorio.save(banner);

                return ResponseEntity.status(HttpStatus.CREATED).body("Imágenes subidas con éxito");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // Método para extraer el ID del administrador desde el token JWT
 private Integer extraerAdminIdDesdeToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Elimina el prefijo "Bearer "
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("adminId").asInt();
        } else {
            throw new JWTDecodeException("Token JWT inválido");
        }
    }
}