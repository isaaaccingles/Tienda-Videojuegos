package com.tiendavideojuegos.tienda.Controllers;

import com.tiendavideojuegos.tienda.Models.UserModel;
import com.tiendavideojuegos.tienda.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "http://localhost:3001") 
@RequestMapping("/gamenest/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        logger.info("Obteniendo todos los usuarios...");
        List<UserModel> users = userService.getAllUsers();
        List<Map<String, Object>> usersWithoutPassword = users.stream().map(user -> {
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole());
            return userData;
        }).collect(Collectors.toList());
        logger.info("Número de usuarios encontrados: {}", usersWithoutPassword.size());
        return ResponseEntity.ok(usersWithoutPassword);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserModel user, BindingResult result) {
        logger.info("Intentando registrar un nuevo usuario: {}", user.getUsername());
    
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            logger.error("Errores al registrar usuario: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }
    
        // Comprobar si el username ya está registrado
        Optional<UserModel> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            logger.warn("El nombre de usuario '{}' ya está en uso.", user.getUsername());
            return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso.");
        }
    
        // Comprobar si el email ya está registrado
        Optional<UserModel> existingEmail = userService.findByEmail(user.getEmail());
        if (existingEmail.isPresent()) {
            logger.warn("El correo electrónico '{}' ya está registrado.", user.getEmail());
            return ResponseEntity.badRequest().body("El correo electrónico ya está registrado.");
        }
    
        // Asignar el rol por defecto si no se especifica (en este caso 'USER')
        if (user.getRole() == null) {
            user.setRole(UserModel.Role.USER); // Asignar el rol 'USER' por defecto
        }
    
        // Registrar el nuevo usuario
        UserModel savedUser = userService.registerUser(user);
        logger.info("Usuario registrado con éxito: {}", savedUser.getUsername());
    
        return ResponseEntity.ok(Map.of(
            "id", savedUser.getId(),
            "username", savedUser.getUsername(),
            "email", savedUser.getEmail(),
            "role", savedUser.getRole()
        ));
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserModel user) {
        logger.info("Intentando iniciar sesión para el usuario con correo: {}", user.getEmail());

        // Buscar el usuario por el correo electrónico
        Optional<UserModel> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isEmpty()) {
        logger.warn("Credenciales incorrectas para el correo: {}", user.getEmail());
        return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas."));
        }

        // Validar la contraseña
        boolean isPasswordValid = userService.validatePassword(user.getPassword(), existingUser.get().getPassword());
        if (!isPasswordValid) {
        logger.warn("Contraseña incorrecta para el usuario con correo: {}", user.getEmail());
        return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas."));
        }

        // Generar el token
        String token = userService.generateToken(existingUser.get()); // Pasar UserModel
        logger.info("Autenticación exitosa para el usuario con correo: {}", user.getEmail());

        return ResponseEntity.ok(Map.of(
        "message", "Autenticación exitosa.",
        "token", token,
        "id", existingUser.get().getId(),
        "username", existingUser.get().getUsername(),
        "email", existingUser.get().getEmail(),
        "role", existingUser.get().getRole()
        ));
  }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        logger.info("Obteniendo datos del usuario con ID: {}", id);

        Optional<UserModel> userOpt = userService.findById(id);

        if (userOpt.isEmpty()) {
            logger.warn("Usuario con ID '{}' no encontrado.", id);
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado."));
        }

        UserModel user = userOpt.get();
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("username", user.getUsername());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole());

        logger.info("Datos del usuario con ID '{}' obtenidos con éxito.", id);
        return ResponseEntity.ok(userData);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Intentando eliminar el usuario con ID: {}", id);

        Optional<UserModel> user = userService.findById(id);
        if (user.isEmpty()) {
            logger.warn("Usuario con ID '{}' no encontrado para eliminar.", id);
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado."));
        }

        userService.deleteUser(id);
        logger.info("Usuario con ID '{}' eliminado correctamente.", id);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente."));
    }
}
