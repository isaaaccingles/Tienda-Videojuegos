package com.tiendavideojuegos.tienda.Controllers;

import java.util.List;

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
    public ResponseEntity<CarritoModel> addToCart(
            @RequestParam Long userId,
            @RequestParam Long videojuegoId,
            @RequestParam Integer cantidad) {
        System.out.println("Llegó a /agregar con userId: " + userId + ", videojuegoId: " + videojuegoId + ", cantidad: " + cantidad);
        try {
            CarritoModel carrito = carritoService.addToCart(userId, videojuegoId, cantidad);
            return ResponseEntity.ok(carrito);
        } catch (RuntimeException e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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

