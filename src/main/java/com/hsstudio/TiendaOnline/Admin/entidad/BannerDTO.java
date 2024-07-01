package com.hsstudio.TiendaOnline.Admin.entidad;

public class BannerDTO {
    private Integer id;
    private String imagen;
    
       private Integer posicion;

    public BannerDTO(Integer id, String imagen) {
        this.id = id;
        this.imagen = imagen;
    }
    public BannerDTO() {
       
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    
    

    @Override
    public String toString() {
        return "BannerDTO{" +
               "id=" + id +
               ", imagenLength=" + (imagen != null ? imagen.length() : 0) +
               '}';
    }
}