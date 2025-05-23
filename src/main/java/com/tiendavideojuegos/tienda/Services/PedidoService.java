package com.tiendavideojuegos.tienda.Services;

import com.tiendavideojuegos.tienda.Models.PedidoModel;
import com.tiendavideojuegos.tienda.Models.UserModel;
import com.tiendavideojuegos.tienda.Repositories.PedidoRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PedidoService {
  private final UserService userService; // Para validar el usuario
  private final PedidoRepository pedidoRepository;

  public PedidoService(UserService userService, VideojuegoService videojuegoService, PedidoRepository pedidoRepository) {
    this.userService = userService;
    this.pedidoRepository = pedidoRepository;
  }

  public PedidoModel crearPedido(PedidoModel pedido) {
    System.out.println("PedidoService - Creando pedido para nombreUsuario: " + pedido.getNombreUsuario());

    // Validar nombreUsuario
    Optional<UserModel> user = userService.findByUsername(pedido.getNombreUsuario());
    if (user.isEmpty()) {
      throw new IllegalArgumentException("Usuario con username " + pedido.getNombreUsuario() + " no encontrado");
    }

    // Opcional: Validar idUsuario si se proporciona
    if (pedido.getIdUsuario() != null) {
      Optional<UserModel> userById = userService.findById(pedido.getIdUsuario());
      if (userById.isEmpty() || !userById.get().getUsername().equals(pedido.getNombreUsuario())) {
        throw new IllegalArgumentException("ID de usuario " + pedido.getIdUsuario() + " no coincide con el username " + pedido.getNombreUsuario());
      }
    } else {
      // Si no se proporciona idUsuario, usar el del usuario encontrado
      pedido.setIdUsuario(user.get().getId());
    }

    // Guardar el pedido en la base de datos
    return pedidoRepository.save(pedido);
  }
}