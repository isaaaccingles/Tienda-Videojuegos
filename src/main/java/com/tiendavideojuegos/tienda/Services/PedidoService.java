package com.tiendavideojuegos.tienda.Services;

import com.tiendavideojuegos.tienda.Models.PedidoModel;
import com.tiendavideojuegos.tienda.Models.UserModel;
import com.tiendavideojuegos.tienda.Repositories.PedidoRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class PedidoService {
    private final UserService userService;
    private final PedidoRepository pedidoRepository;

    public PedidoService(UserService userService, VideojuegoService videojuegoService, PedidoRepository pedidoRepository) {
        this.userService = userService;
        this.pedidoRepository = pedidoRepository;
    }

    public PedidoModel crearPedido(PedidoModel pedido) {
        System.out.println("PedidoService - Creando pedido para idUsuario: " + pedido.getIdUsuario());

        // Validar idUsuario
        if (pedido.getIdUsuario() == null) {
            throw new IllegalArgumentException("ID de usuario es requerido");
        }

        // Verificar que el usuario existe
        Optional<UserModel> user = userService.findById(pedido.getIdUsuario());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Usuario con ID " + pedido.getIdUsuario() + " no encontrado");
        }

        // Guardar el pedido en la base de datos
        return pedidoRepository.save(pedido);
    }
}