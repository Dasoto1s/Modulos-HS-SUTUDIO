package com.hsstudio.TiendaOnline.Admin.entidad;

public class ProductoDTO {
    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private float precio;
    private int talla;
    private String color;
    private String genero;
    private String tipoZapato;
    private Float cantidad;
    private Integer stock;
    private Integer cantidadMinimaRequerida;

    private byte[] imagen;

    public ProductoDTO(Integer idProducto, String nombre, String descripcion, float precio, int talla, String color, String genero, String tipoZapato, Float cantidad, Integer stock, Integer cantidadMinimaRequerida, byte[] imagen) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.talla = talla;
        this.color = color;
        this.genero = genero;
        this.tipoZapato = tipoZapato;
        this.cantidad = cantidad;
        this.stock = stock;
        this.cantidadMinimaRequerida = cantidadMinimaRequerida;
        this.imagen = imagen;
    }

   

    public ProductoDTO() {
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getTalla() {
        return talla;
    }

    public void setTalla(int talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTipoZapato() {
        return tipoZapato;
    }

    public void setTipoZapato(String tipoZapato) {
        this.tipoZapato = tipoZapato;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

  public Float getCantidad() {
        return cantidad;
    }

    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getCantidadMinimaRequerida() {
        return cantidadMinimaRequerida;
    }

    public void setCantidadMinimaRequerida(Integer cantidadMinimaRequerida) {
        this.cantidadMinimaRequerida = cantidadMinimaRequerida;
    }
    
}
