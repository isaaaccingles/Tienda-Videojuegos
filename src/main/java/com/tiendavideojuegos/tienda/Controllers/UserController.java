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

@RestController
@RequestMapping("/gamenest/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();

        // Excluir la contraseña en la respuesta
        List<Map<String, Object>> usersWithoutPassword = users.stream().map(user -> {
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole());
            return userData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(usersWithoutPassword);
    }

    // Registro de un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserModel user, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        if (userService.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso.");
        }

        if (userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("El correo electrónico ya está registrado.");
        }

        UserModel savedUser = userService.registerUser(user);
        return ResponseEntity.ok(Map.of(
            "id", savedUser.getId(),
            "username", savedUser.getUsername(),
            "email", savedUser.getEmail(),
            "role", savedUser.getRole()
        ));
    }

    // Autenticación de un usuario
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserModel user) {
        UserModel existingUser = userService.findByUsername(user.getUsername());
        
        if (existingUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas."));
        }

        boolean isPasswordValid = userService.validatePassword(user.getPassword(), existingUser.getPassword());
        if (!isPasswordValid) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas."));
        }

        return ResponseEntity.ok(Map.of(
            "message", "Autenticación exitosa.",
            "id", existingUser.getId(),
            "username", existingUser.getUsername(),
            "email", existingUser.getEmail(),
            "role", existingUser.getRole()
        ));
    }

    // Eliminar usuario (solo ADMIN)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<UserModel> user = userService.findById(id);
        if (!user.isPresent()) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado."));
        }
        
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente."));
    }
}
