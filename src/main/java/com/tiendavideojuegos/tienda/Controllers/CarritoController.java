package com.tiendavideojuegos.tienda.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.tiendavideojuegos.tienda.Models.CarritoDTO;
import com.tiendavideojuegos.tienda.Models.CarritoModel;
import com.tiendavideojuegos.tienda.Services.CarritoService;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
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
    
            CarritoDTO dto = new CarritoDTO(
                carrito.getId(),
                carrito.getCantidad(),
                carrito.getVideojuego().getId(),
                carrito.getVideojuego().getTitulo(),
                carrito.getVideojuego().getPrecio(),
                carrito.getVideojuego().getImagenUrl()
            );
    
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", dto
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
        } catch (ConstraintViolationException e) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Validación fallida",
                "details", errors
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error de integridad de datos",
                "details", e.getRootCause().getMessage()
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

