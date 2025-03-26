package com.tiendavideojuegos.tienda.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiendavideojuegos.tienda.Models.GenerosModel;

@Repository
public interface GenerosRepository extends JpaRepository<GenerosModel, Long> {
    // Buscar género por ID
    @SuppressWarnings("null")
    Optional<GenerosModel> findById(Long id); 
}
