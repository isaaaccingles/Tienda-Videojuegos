package com.tiendavideojuegos.tienda.Services;

import com.tiendavideojuegos.tienda.Models.UserModel;
import com.tiendavideojuegos.tienda.Repositories.UserRepository;
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
        return userRepository.findAll();  // Obtener todos los usuarios
    }

    // Buscar un usuario por ID
    public Optional<UserModel> findById(Long id) {
        return userRepository.findById(id);  // Este método es proporcionado por JpaRepository
    }

    // Registrar un nuevo usuario con la contraseña encriptada
    public UserModel registerUser(UserModel user) {
        // Encriptar la contraseña antes de guardarla
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        
        // Guardar el usuario en la base de datos
        return userRepository.save(user);
    }

    // Buscar usuario por nombre de usuario
    public UserModel findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Buscar usuario por correo electrónico
    public UserModel findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Método para verificar si la contraseña es correcta al autenticar
    public boolean validatePassword(String rawPassword, String encryptedPassword) {
        return passwordEncoder.matches(rawPassword, encryptedPassword);
    }

    // Método para eliminar un usuario por su ID
    public void deleteUser(Long id) {
        // Verificar si el usuario existe antes de eliminarlo (opcional, pero recomendable)
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }

}
