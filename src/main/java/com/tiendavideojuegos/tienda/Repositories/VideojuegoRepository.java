package com.tiendavideojuegos.tienda.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiendavideojuegos.tienda.Models.VideojuegoModel;

@Repository
public interface VideojuegoRepository extends JpaRepository<VideojuegoModel, Long> {
    @SuppressWarnings("null")
    Optional<VideojuegoModel> findById(Long id);
    Optional<VideojuegoModel> findByTitulo(String titulo);
    List<VideojuegoModel> findByDesarrollador(String desarrollador);
    List<VideojuegoModel> findByCalificacionEdad(String calificacionEdad);

    // Buscar videojuegos por ID de plataforma
    List<VideojuegoModel> findByPlataformas_Id(Long plataformaId); 

    // Buscar videojuegos por ID de género
    List<VideojuegoModel> findByGeneros_Id(Long generoId); 
}

