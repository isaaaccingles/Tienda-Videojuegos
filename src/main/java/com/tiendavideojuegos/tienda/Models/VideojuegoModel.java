package com.tiendavideojuegos.tienda.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@JsonIgnoreProperties({"plataformasIds", "generosIds"})
@Table(name = "videojuegos")
public class VideojuegoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El título no puede ser nulo")
    @Size(min = 2, max = 100, message = "El título debe tener entre 2 y 100 caracteres")
    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.01", inclusive = false, message = "El precio debe ser mayor que 0")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "fecha_lanzamiento")
    private LocalDate fechaLanzamiento;

    @Size(max = 100, message = "El desarrollador no puede tener más de 100 caracteres")
    @Column(name = "desarrollador", length = 100)
    private String desarrollador;

    @Size(max = 100, message = "El editor no puede tener más de 100 caracteres")
    @Column(name = "editor", length = 100)
    private String editor;

    @Size(max = 10, message = "La calificación de edad no puede tener más de 10 caracteres")
    @Pattern(regexp = "^[EMT][0-9]+\\+$", message = "Formato inválido") 
    @Column(name = "calificacion_edad", length = 10)
    private String calificacionEdad;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    private String imagen_url;

    @ManyToMany
    @JoinTable(
        name = "videojuegos_plataformas", 
        joinColumns = @JoinColumn(name = "videojuego_id"), 
        inverseJoinColumns = @JoinColumn(name = "plataforma_id")
    )
    @JsonManagedReference 
    private List<PlataformaModel> plataformas;

    @ManyToMany
    @JoinTable(
        name = "videojuegos_generos", 
        joinColumns = @JoinColumn(name = "videojuego_id"), 
        inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    @JsonManagedReference 
    private List<GenerosModel> generos;

    // Nuevas propiedades para los IDs de plataformas y géneros
    @Transient
    private List<Long> plataformasIds;

    @Transient
    private List<Long> generosIds;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public LocalDate getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(LocalDate fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public String getDesarrollador() {
        return desarrollador;
    }

    public void setDesarrollador(String desarrollador) {
        this.desarrollador = desarrollador;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getCalificacionEdad() {
        return calificacionEdad;
    }

    public void setCalificacionEdad(String calificacionEdad) {
        this.calificacionEdad = calificacionEdad;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<PlataformaModel> getPlataformas() {
        return plataformas;
    }

    public void setPlataformas(List<PlataformaModel> plataformas) {
        this.plataformas = plataformas;
    }

    public List<GenerosModel> getGeneros() {
        return generos;
    }

    public void setGeneros(List<GenerosModel> generos) {
        this.generos = generos;
    }

    // Getters y Setters para los IDs de plataformas y géneros
    public List<Long> getPlataformasIds() {
        return plataformasIds;
    }

    public void setPlataformasIds(List<Long> plataformasIds) {
        this.plataformasIds = plataformasIds;
    }

    public List<Long> getGenerosIds() {
        return generosIds;
    }

    public void setGenerosIds(List<Long> generosIds) {
        this.generosIds = generosIds;
    }

    public String getImagenUrl() {
        return imagen_url;
    }

    public void setImagenUrl(String imagen_url) {
        this.imagen_url = imagen_url;
    }

    @Override
    public String toString() {
        return "VideojuegoModel{" +
               "id=" + id +
               ", titulo='" + titulo + '\'' +
               ", descripcion='" + descripcion + '\'' +
               ", precio=" + precio +
               ", stock=" + stock +
               ", fechaLanzamiento=" + fechaLanzamiento +
               ", desarrollador='" + desarrollador + '\'' +
               ", editor='" + editor + '\'' +
               ", calificacionEdad='" + calificacionEdad + '\'' +
               ", plataformas=" + plataformas +
               ", generos=" + generos +
               '}';
    }
}
