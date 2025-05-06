package com.tiendavideojuegos.tienda.Controllers;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tiendavideojuegos.tienda.Exceptions.ConflictException;
import com.tiendavideojuegos.tienda.Exceptions.ResourceNotFoundException;
import com.tiendavideojuegos.tienda.Models.CarritoModel;
import com.tiendavideojuegos.tienda.Services.CarritoService;

@RestController
@CrossOrigin(origins = "http://localhost:3001")
@RequestMapping("/gamenest/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/{userId}")
    public List<CarritoModel> getCart(@PathVariable Long userId) {
        return carritoService.getCartByUser(userId);
    }

@PostMapping("/agregar")
public ResponseEntity<?> addToCart(
        @RequestParam(required = true) Long userId,
        @RequestParam(required = true) Long videojuegoId,
        @RequestParam(required = true) Integer cantidad) {
    
    System.out.println("Inicio addToCart - userId: " + userId + ", videojuegoId: " + videojuegoId);
    
    try {
        // Validación básica de parámetros
        if (userId == null || videojuegoId == null || cantidad == null || cantidad <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Parámetros inválidos",
                "details", Map.of(
                    "userId", userId,
                    "videojuegoId", videojuegoId,
                    "cantidad", cantidad
                )
            ));
        }

        CarritoModel carrito = carritoService.addToCart(userId, videojuegoId, cantidad);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", carrito
        ));
        
    } catch (ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(Map.of(
            "error", e.getMessage(),
            "type", "NOT_FOUND"
        ));
    } catch (ConflictException e) {
        return ResponseEntity.status(HttpStatus.SC_CONFLICT).body(Map.of(
            "error", e.getMessage(),
            "type", "CONFLICT"
        ));
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body(Map.of(
            "error", "Error interno del servidor",
            "details", e.getMessage()
        ));
    }
}
    
    @DeleteMapping("/eliminar/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long itemId) {
        carritoService.removeFromCart(itemId);
        return ResponseEntity.ok().body("Item eliminado del carrito");
    }

    @DeleteMapping("/limpiar/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        carritoService.clearCart(userId);
        return ResponseEntity.ok().body("Carrito vaciado");
    }
}

