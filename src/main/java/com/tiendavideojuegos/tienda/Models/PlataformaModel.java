package com.tiendavideojuegos.tienda.Models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "plataformas")
// @JsonIgnoreProperties({"id", "videojuegos"})
public class PlataformaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El nombre de la plataforma no puede ser nulo")
    @Size(min = 2, max = 100, message = "El nombre de la plataforma debe tener entre 2 y 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @ManyToMany(mappedBy = "plataformas")
    @JsonBackReference
    private List<VideojuegoModel> videojuegos;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<VideojuegoModel> getVideojuegos() {
        return videojuegos;
    }

    public void setVideojuegos(List<VideojuegoModel> videojuegos) {
        this.videojuegos = videojuegos;
    }
}
