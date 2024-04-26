package com.hsstudio.TiendaOnline.Admin.controlador;

import com.hsstudio.TiendaOnline.Admin.entidad.Banner;
import com.hsstudio.TiendaOnline.Admin.repositorio.BannerRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/banner")
public class BannerControlador {
    private final BannerRepositorio bannerRepositorio;

    @Autowired
    public BannerControlador(BannerRepositorio bannerRepositorio) {
        this.bannerRepositorio = bannerRepositorio;
    }

    // Subir nuevas imágenes al banner
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImages(@RequestParam("imagen1") MultipartFile imagen1,
                                          @RequestParam("imagen2") MultipartFile imagen2,
                                          @RequestParam("imagen3") MultipartFile imagen3) throws Exception {
        Banner banner = new Banner();
        banner.setImagen1(imagen1.getBytes());
        banner.setImagen2(imagen2.getBytes());
        banner.setImagen3(imagen3.getBytes());

        bannerRepositorio.save(banner);

        return ResponseEntity.status(HttpStatus.CREATED).body("Imágenes subidas con éxito");
    }
    
        
    
    
}
