package com.hsstudio.TiendaOnline.Admin.entidad;

import jakarta.persistence.*;
@Entity
@Table(name = "Banner")
public class Banner {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_publicidad")
    private Integer id;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imagen;

    @Column(name = "posicion")
    private Integer posicion; 
    
    @ManyToOne  
    @JoinColumn(name = "Id_administrador2", referencedColumnName = "Id_administrador")
    private Admin admin;

    public Banner(Integer id, byte[] imagen, Admin admin) {
        this.id = id;
        this.imagen = imagen;
        this.admin = admin;
    }
    
    public Banner() {
        
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    
    
}