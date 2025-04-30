package com.tiendavideojuegos.tienda.Repositories;

import com.tiendavideojuegos.tienda.Models.PlataformaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlataformaRepository extends JpaRepository<PlataformaModel, Long> {
    // Buscar plataforma por ID
    Optional<PlataformaModel> findById(Long id);

}
