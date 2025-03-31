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

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();
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

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserModel user, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        Optional<UserModel> existingUser = userService.findByUsername(user.getUsername());
        System.out.println("Username: " + user.getUsername() + " - Exists: " + existingUser.isPresent());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso.");
        }

        Optional<UserModel> existingEmail = userService.findByEmail(user.getEmail());
        System.out.println("Email: " + user.getEmail() + " - Exists: " + existingEmail.isPresent());
        if (existingEmail.isPresent()) {
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

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserModel user) {
        Optional<UserModel> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas."));
        }

        boolean isPasswordValid = userService.validatePassword(user.getPassword(), existingUser.get().getPassword());
        if (!isPasswordValid) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas."));
        }

        String token = userService.generateToken(existingUser.get().getUsername());
        return ResponseEntity.ok(Map.of(
            "message", "Autenticación exitosa.",
            "token", token,
            "id", existingUser.get().getId(),
            "username", existingUser.get().getUsername(),
            "email", existingUser.get().getEmail(),
            "role", existingUser.get().getRole()
        ));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<UserModel> user = userService.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado."));
        }

        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente."));
    }
}