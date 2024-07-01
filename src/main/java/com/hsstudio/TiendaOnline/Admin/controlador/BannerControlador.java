package com.hsstudio.TiendaOnline.Admin.controlador;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import com.hsstudio.TiendaOnline.Admin.entidad.Banner;
import com.hsstudio.TiendaOnline.Admin.entidad.BannerDTO;
import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.BannerRepositorio;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/banner")
@CrossOrigin(origins = "http://localhost:3000")
public class BannerControlador {

    private final BannerRepositorio bannerRepositorio;
    private final AdminRepositorio adminRepositorio;

    @Autowired
    public BannerControlador(BannerRepositorio bannerRepositorio, AdminRepositorio adminRepositorio) {
        this.bannerRepositorio = bannerRepositorio;
        this.adminRepositorio = adminRepositorio;
    }

  @GetMapping
public ResponseEntity<List<BannerDTO>> getAllBanners() {
    List<Banner> banners = bannerRepositorio.findAll();
    List<BannerDTO> bannerDTOs = banners.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    return ResponseEntity.ok(bannerDTOs);
}

    @GetMapping("/{id}")
    public ResponseEntity<BannerDTO> getBannerById(@PathVariable Integer id) {
        Optional<Banner> bannerOptional = bannerRepositorio.findById(id);
        if (bannerOptional.isPresent()) {
            BannerDTO bannerDTO = convertToDTO(bannerOptional.get());
            return ResponseEntity.ok(bannerDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id}")
public ResponseEntity<?> updateBannerImage(
        @RequestHeader("Authorization") String authorizationHeader,
        @PathVariable Integer id,
        @RequestParam("imagen") MultipartFile imagen) throws Exception {
    if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
        Integer adminId = extraerAdminIdDesdeToken(authorizationHeader);
        Optional<Admin> adminOptional = adminRepositorio.findById(adminId);

        if (adminOptional.isPresent()) {
            Optional<Banner> bannerOptional = bannerRepositorio.findById(id);

            if (bannerOptional.isPresent()) {
                Banner banner = bannerOptional.get();

                if (imagen.isEmpty()) {
                    return ResponseEntity.badRequest().body("Se requiere una imagen válida");
                }

                byte[] optimizedImagen = optimizeImage(imagen.getBytes());
                banner.setImagen(optimizedImagen);
                bannerRepositorio.save(banner);

                return ResponseEntity.ok("Imagen actualizada con éxito");
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestHeader("Authorization") String authorizationHeader,
                                         @RequestParam("imagen") MultipartFile imagen,
                                          @RequestParam("posicion") Integer posicion) throws Exception {
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            Integer adminId = extraerAdminIdDesdeToken(authorizationHeader);
            Optional<Admin> adminOptional = adminRepositorio.findById(adminId);

            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();

                if (imagen.isEmpty()) {
                    return ResponseEntity.badRequest().body("Se requiere una imagen válida");
                }

                byte[] optimizedImagen = optimizeImage(imagen.getBytes());

                Banner banner = new Banner();
    banner.setImagen(optimizedImagen);
    banner.setAdmin(admin);
    banner.setPosicion(posicion);

    bannerRepositorio.save(banner);

                return ResponseEntity.status(HttpStatus.CREATED).body("Imagen subida con éxito");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

   private BannerDTO convertToDTO(Banner banner) {
    BannerDTO dto = new BannerDTO();
    dto.setId(banner.getId());
    dto.setImagen(Base64.getEncoder().encodeToString(banner.getImagen()));
    dto.setPosicion(banner.getPosicion());
    return dto;
}

private byte[] optimizeImage(byte[] imageData) throws IOException {
    if (imageData == null || imageData.length == 0) {
        throw new IllegalArgumentException("Image data cannot be null or empty");
    }
    
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
    if (image == null) {
        throw new IOException("Failed to read image data");
    }
    
    BufferedImage resizedImage = Scalr.resize(image, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, 800, 600);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(resizedImage, "jpg", baos);
    baos.flush();
    byte[] compressedImage = baos.toByteArray();
    baos.close();

    return compressedImage;
}
    public   Integer extraerAdminIdDesdeToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT.getClaim("adminId").asInt();
        } else {
            throw new JWTDecodeException("Token JWT inválido");
        }
    }
}