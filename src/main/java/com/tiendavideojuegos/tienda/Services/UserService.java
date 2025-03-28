package com.tiendavideojuegos.tienda.Services;

import com.tiendavideojuegos.tienda.Models.UserModel;
import com.tiendavideojuegos.tienda.Repositories.UserRepository;
import jakarta.transaction.Transactional;  // Import correcto para transacciones en Spring Boot
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Usar BCryptPasswordEncoder para encriptar contraseñas
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Método para obtener todos los usuarios
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    // Buscar un usuario por ID
    public Optional<UserModel> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserModel> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    

    public Optional<UserModel> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    

    // Método para registrar un nuevo usuario con la contraseña encriptada
    @Transactional
    public UserModel registerUser(UserModel user) {
        // Verificar si el usuario ya existe
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("El usuario ya existe");
        }

        // Encriptar la contraseña antes de guardarla
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    // Método para verificar si la contraseña es correcta al autenticar
    public boolean validatePassword(String rawPassword, String encryptedPassword) {
        return passwordEncoder.matches(rawPassword, encryptedPassword);
    }

    // Método para eliminar un usuario por su ID
    @Transactional
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }
}
