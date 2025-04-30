package com.tiendavideojuegos.tienda.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tiendavideojuegos.tienda.Exceptions.ConflictException;
import com.tiendavideojuegos.tienda.Exceptions.ResourceNotFoundException;
import com.tiendavideojuegos.tienda.Models.CarritoModel;
import com.tiendavideojuegos.tienda.Models.UserModel;
import com.tiendavideojuegos.tienda.Models.VideojuegoModel;
import com.tiendavideojuegos.tienda.Repositories.CarritoRepository;
import com.tiendavideojuegos.tienda.Repositories.UserRepository;
import com.tiendavideojuegos.tienda.Repositories.VideojuegoRepository;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepo;

    @Autowired
    private VideojuegoRepository videojuegoRepo;

    @Autowired
    private UserRepository userRepository;

    // Obtener el carrito de un usuario por su ID
    public List<CarritoModel> getCartByUser(Long userId) {
        return carritoRepo.findByUserId(userId);
    }

    public CarritoModel addToCart(Long userId, Long videojuegoId, Integer cantidad) {
        System.out.println("-> addToCart: userId=" + userId + ", videojuegoId=" + videojuegoId + ", cantidad=" + cantidad);
    
        try {
            // Buscar si el item ya existe en el carrito
            CarritoModel item = carritoRepo.findByUserIdAndVideojuegoId(userId, videojuegoId)
                    .orElse(null); // Si no existe, item será null
    
            // Buscar al usuario
            UserModel user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            System.out.println("Usuario encontrado: " + user.getUsername());
    
            // Buscar el videojuego
            VideojuegoModel videojuego = videojuegoRepo.findById(videojuegoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Videojuego no encontrado"));
            System.out.println("Videojuego encontrado: " + videojuego.getTitulo());
    
            // Verificar si hay suficiente stock
            if (videojuego.getStock() < cantidad) {
                throw new ConflictException("No hay suficiente stock del videojuego para esta cantidad.");
            }

            if (item == null) {
                // Si el item no existe, crear uno nuevo
                item = new CarritoModel();
                item.setUser(user);
                item.setVideojuego(videojuego);
                item.setCantidad(cantidad); // Establecer la cantidad inicial
            } else {
                // Si el item ya existe, actualizar la cantidad
                int nuevaCantidad = item.getCantidad() + cantidad; // Sumamos la cantidad
                item.setCantidad(nuevaCantidad);
            }

            // Reducir el stock del videojuego
            videojuego.setStock(videojuego.getStock() - cantidad);
            videojuegoRepo.save(videojuego);

            // Guardar el item en el carrito
            CarritoModel result = carritoRepo.save(item);
    
            System.out.println("Producto agregado al carrito con éxito.");
            return result;
    
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Dejar que el controller capture el error
        }
    }

    // Eliminar un producto del carrito
    public void removeFromCart(Long itemId) {
        // Buscar el item y eliminarlo
        CarritoModel item = carritoRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado en el carrito"));
        carritoRepo.delete(item);
        
        // Restaurar el stock
        VideojuegoModel videojuego = item.getVideojuego();
        videojuego.setStock(videojuego.getStock() + item.getCantidad());
        videojuegoRepo.save(videojuego);
    }

    // Vaciar el carrito de un usuario
    public void clearCart(Long userId) {
        List<CarritoModel> items = carritoRepo.findByUserId(userId);
        for (CarritoModel item : items) {
            carritoRepo.delete(item);
            
            // Restaurar el stock de cada producto en el carrito
            VideojuegoModel videojuego = item.getVideojuego();
            videojuego.setStock(videojuego.getStock() + item.getCantidad());
            videojuegoRepo.save(videojuego);
        }
    }
}
