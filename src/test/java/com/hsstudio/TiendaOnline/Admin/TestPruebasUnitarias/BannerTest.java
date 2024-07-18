package com.hsstudio.TiendaOnline.Admin.TestPruebasUnitarias;

import com.hsstudio.TiendaOnline.Admin.controlador.BannerControlador;
import com.hsstudio.TiendaOnline.Admin.controlador.BannerControlador;
import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import com.hsstudio.TiendaOnline.Admin.entidad.Admin;
import com.hsstudio.TiendaOnline.Admin.entidad.Banner;
import com.hsstudio.TiendaOnline.Admin.entidad.Banner;
import com.hsstudio.TiendaOnline.Admin.entidad.BannerDTO;
import com.hsstudio.TiendaOnline.Admin.entidad.BannerDTO;
import com.hsstudio.TiendaOnline.Admin.repositorio.AdminRepositorio;
import com.hsstudio.TiendaOnline.Admin.repositorio.BannerRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BannerTest {

    @InjectMocks
    private BannerControlador bannerControlador;

    @Mock
    private BannerRepositorio bannerRepositorio;

    @Mock
    private AdminRepositorio adminRepositorio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private byte[] createValidImageData() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }

    @Test
    void getAllBanners() {
        Banner banner1 = new Banner();
        banner1.setId(1);
        banner1.setImagen(new byte[]{1, 2, 3});
        banner1.setPosicion(1);

        Banner banner2 = new Banner();
        banner2.setId(2);
        banner2.setImagen(new byte[]{4, 5, 6});
        banner2.setPosicion(2);

        when(bannerRepositorio.findAll()).thenReturn(Arrays.asList(banner1, banner2));

        ResponseEntity<List<BannerDTO>> response = bannerControlador.getAllBanners();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getBannerById() {
        Banner banner = new Banner();
        banner.setId(1);
        banner.setImagen(new byte[]{1, 2, 3});
        banner.setPosicion(1);

        when(bannerRepositorio.findById(1)).thenReturn(Optional.of(banner));

        ResponseEntity<BannerDTO> response = bannerControlador.getBannerById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getId());
    }

    @Test
    void updateBannerImage() throws Exception {
        String authHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZG1pbklkIjoxfQ.ZQiWUPYcn1xrhY1HyVyL9w7lyXhcFVm8LlkLDe3UVf4";
        byte[] imageData = createValidImageData();
        MockMultipartFile file = new MockMultipartFile("imagen", "test.jpg", "image/jpeg", imageData);

        Admin admin = new Admin();
        admin.setId(1);

        Banner banner = new Banner();
        banner.setId(1);

        when(adminRepositorio.findById(1)).thenReturn(Optional.of(admin));
        when(bannerRepositorio.findById(1)).thenReturn(Optional.of(banner));

        BannerControlador spyController = spy(bannerControlador);
        doReturn(1).when(spyController).extraerAdminIdDesdeToken(authHeader);

        ResponseEntity<?> response = spyController.updateBannerImage(authHeader, 1, file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void uploadImage() throws Exception {
        String authHeader = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZG1pbklkIjoxfQ.ZQiWUPYcn1xrhY1HyVyL9w7lyXhcFVm8LlkLDe3UVf4";
        byte[] imageData = createValidImageData();
        MockMultipartFile file = new MockMultipartFile("imagen", "test.jpg", "image/jpeg", imageData);

        Admin admin = new Admin();
        admin.setId(1);

        when(adminRepositorio.findById(1)).thenReturn(Optional.of(admin));

        BannerControlador spyController = spy(bannerControlador);
        doReturn(1).when(spyController).extraerAdminIdDesdeToken(authHeader);

        ResponseEntity<?> response = spyController.uploadImage(authHeader, file, 1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}