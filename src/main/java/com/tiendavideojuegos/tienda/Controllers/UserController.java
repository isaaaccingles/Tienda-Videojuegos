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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gamenest/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();
        
        // Excluir la contraseña en la respuesta
        List<UserModel> usersWithoutPassword = users.stream().map(user -> {
            user.setPassword(null);  // Remover la contraseña
            return user;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(usersWithoutPassword);
    }

    // Registro de un nuevo usuario
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserModel user, BindingResult result) {
        // Verificar si hay errores de validación
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        // Verificar si el usuario ya existe
        if (userService.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya está en uso.");
        }

        if (userService.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("El correo electrónico ya está registrado.");
        }

        // Registrar al usuario
        UserModel savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    // Autenticación de un usuario
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserModel user) {
        // Buscar el usuario por nombre de usuario
        UserModel existingUser = userService.findByUsername(user.getUsername());
        if (existingUser == null) {
            return ResponseEntity.status(401).body("Credenciales incorrectas.");
        }

        // Verificar la contraseña encriptada
        boolean isPasswordValid = userService.validatePassword(user.getPassword(), existingUser.getPassword());
        
        if (!isPasswordValid) {
            return ResponseEntity.status(401).body("Credenciales incorrectas.");
        }
        
        return ResponseEntity.ok("Autenticación exitosa.");
    }
}
