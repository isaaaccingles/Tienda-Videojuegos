package com.tiendavideojuegos.tienda.Models;

import java.math.BigDecimal;

public class CarritoDTO {
    private Long id;
    private int cantidad;
    private Long videojuegoId;
    private String titulo;
    private BigDecimal precio;
    private String imagenUrl;

    public CarritoDTO(Long id, int cantidad, Long videojuegoId, String titulo, BigDecimal precio, String imagenUrl) {
        this.id = id;
        this.cantidad = cantidad;
        this.videojuegoId = videojuegoId;
        this.titulo = titulo;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public Long getVideojuegoId() {
        return videojuegoId;
    }
    public void setVideojuegoId(Long videojuegoId) {
        this.videojuegoId = videojuegoId;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public String getImagenUrl() {
        return imagenUrl;
    }
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
