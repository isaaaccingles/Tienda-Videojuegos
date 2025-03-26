package com.tiendavideojuegos.tienda.Models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;


// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "generos")
// @JsonIgnoreProperties({"id"})
public class GenerosModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 2, max = 50, message = "El nombre del género debe tener entre 2 y 50 caracteres")
    private String nombre;

    @ManyToMany(mappedBy = "generos")
    @JsonBackReference
    private List<VideojuegoModel> videojuegos;

    // Getters y setters
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
