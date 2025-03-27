package com.tiendavideojuegos.tienda.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tiendavideojuegos.tienda.Models.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    // Puedes agregar consultas personalizadas si es necesario
    UserModel findByUsername(String username);
    UserModel findByEmail(String email);
}
