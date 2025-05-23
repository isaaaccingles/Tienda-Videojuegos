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
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/gamenest/pedidos")
public class PedidoController {
  private final PedidoService pedidoService;

  public PedidoController(PedidoService pedidoService) {
    this.pedidoService = pedidoService;
  }

  @PostMapping
  public ResponseEntity<?> crearPedido(Authentication authentication, @RequestBody PedidoModel pedido) {
    System.out.println("PedidoController - Autenticación: " + (authentication != null ? authentication.getName() : "Ninguna"));
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(401).body(Map.of("message", "Usuario no autenticado"));
    }
    try {
      String username = authentication.getName(); // Obtiene el username del token (ej. "admin")
      System.out.println("PedidoController - Username del token: " + username);
      System.out.println("PedidoController - NombreUsuario del pedido: " + pedido.getNombreUsuario());
      if (!username.equals(pedido.getNombreUsuario())) {
        System.out.println("PedidoController - Nombre de usuario no coincide: token=" + username + ", pedido=" + pedido.getNombreUsuario());
        return ResponseEntity.status(403).body(Map.of("message", "Nombre de usuario no coincide con el autenticado"));
      }
      // Opcional: Validar que idUsuario sea consistente con el usuario, si es necesario
      if (pedido.getIdUsuario() != null) {
        System.out.println("PedidoController - idUsuario del pedido: " + pedido.getIdUsuario());
        // Aquí podrías validar idUsuario contra el usuario en la base de datos, si es crítico
      }
      PedidoModel nuevoPedido = pedidoService.crearPedido(pedido);
      return ResponseEntity.ok(nuevoPedido);
    } catch (IllegalArgumentException e) {
      System.out.println("PedidoController - Error: " + e.getMessage());
      return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
    } catch (Exception e) {
      System.out.println("PedidoController - Error procesando pedido: " + e.getMessage());
      return ResponseEntity.status(500).body(Map.of("message", "Error al procesar el pedido", "details", e.getMessage()));
    }
  }
}