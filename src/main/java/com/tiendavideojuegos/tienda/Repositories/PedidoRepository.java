package com.tiendavideojuegos.tienda.Repositories;

import com.tiendavideojuegos.tienda.Models.PedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoModel, Long> {

}
