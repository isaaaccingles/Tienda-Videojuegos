package com.tiendavideojuegos.tienda.Controllers;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tiendavideojuegos.tienda.Models.PedidoModel;
import com.tiendavideojuegos.tienda.Services.PedidoService;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@RequestMapping("/gamenest/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<?> crearPedido(Authentication authentication, @RequestBody PedidoModel pedido) {
        System.out.println("=== INICIO CREAR PEDIDO ===");
        System.out.println("PedidoController - Autenticación: " + (authentication != null ? authentication.getName() : "Ninguna"));
        
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("PedidoController - Error: Usuario no autenticado");
            return ResponseEntity.status(401).body(Map.of("message", "Usuario no autenticado"));
        }

        try {
            String tokenUserId = authentication.getName(); // "1" from token's sub
            System.out.println("PedidoController - UserId del token: '" + tokenUserId + "'");
            System.out.println("PedidoController - IdUsuario del pedido: " + pedido.getIdUsuario());
            System.out.println("PedidoController - IdVideojuego: " + pedido.getIdVideojuego());
            System.out.println("PedidoController - PrecioTotal: " + pedido.getPrecioTotal());

            // Validar idUsuario contra el sub del token
            if (pedido.getIdUsuario() == null || !tokenUserId.equals(pedido.getIdUsuario().toString())) {
                System.out.println("PedidoController - Error: ID de usuario no coincide:");
                System.out.println("  - Token: '" + tokenUserId + "'");
                System.out.println("  - Pedido: '" + pedido.getIdUsuario() + "'");
                return ResponseEntity.status(403).body(Map.of(
                    "message", "ID de usuario no coincide con el autenticado",
                    "tokenUser", tokenUserId,
                    "pedidoUser", pedido.getIdUsuario()
                ));
            }

            // Otras validaciones
            if (pedido.getIdVideojuego() == null) {
                System.out.println("PedidoController - Error: IdVideojuego es nulo");
                return ResponseEntity.status(400).body(Map.of("message", "ID del videojuego es requerido"));
            }

            if (pedido.getPrecioTotal() == null || pedido.getPrecioTotal().doubleValue() <= 0) {
                System.out.println("PedidoController - Error: Precio total inválido: " + pedido.getPrecioTotal());
                return ResponseEntity.status(400).body(Map.of("message", "Precio total debe ser mayor a 0"));
            }

            System.out.println("PedidoController - Todas las validaciones pasaron, creando pedido...");
            PedidoModel nuevoPedido = pedidoService.crearPedido(pedido);
            System.out.println("PedidoController - Pedido creado exitosamente con ID: " + nuevoPedido.getIdPedido());
            System.out.println("=== FIN CREAR PEDIDO EXITOSO ===");
            
            return ResponseEntity.ok(nuevoPedido);

        } catch (IllegalArgumentException e) {
            System.out.println("PedidoController - Error de validación: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            System.out.println("PedidoController - Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "message", "Error al procesar el pedido", 
                "details", e.getMessage()
            ));
        }
    }
}