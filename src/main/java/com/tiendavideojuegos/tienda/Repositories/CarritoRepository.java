package com.tiendavideojuegos.tienda.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiendavideojuegos.tienda.Models.CarritoModel;

@Repository
public interface CarritoRepository extends JpaRepository<CarritoModel, Long> {
    List<CarritoModel> findByUserId(Long userId);
    Optional<CarritoModel> findByUserIdAndVideojuegoId(Long userId, Long videojuegoId);
}

