package com.tiendavideojuegos.tienda.Controllers;

import com.tiendavideojuegos.tienda.Exceptions.ResourceNotFoundException;
import com.tiendavideojuegos.tienda.Models.GenerosModel;
import com.tiendavideojuegos.tienda.Models.VideojuegoModel;
import com.tiendavideojuegos.tienda.Repositories.GenerosRepository;
import com.tiendavideojuegos.tienda.Repositories.VideojuegoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Géneros", description = "Controlador para gestionar géneros")
@RestController
@RequestMapping("/gamenest/generos")
public class GenerosController {

    @Autowired
    private GenerosRepository generosRepository;

    @Autowired
    private VideojuegoRepository videojuegoRepository;

    // Obtener un género por ID junto con los videojuegos asociados
    @Operation(summary = "Obtener un género por ID", description = "Devuelve los detalles de un género específico junto con los videojuegos asociados")
    @GetMapping("/id")
    public GenerosModel getGeneroById(@RequestParam Long id) throws ResourceNotFoundException {
        // Obtener el género por ID
        GenerosModel genero = generosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un género con el ID: " + id));
        
        // Obtener los videojuegos asociados a este género
        List<VideojuegoModel> videojuegos = videojuegoRepository.findByGeneros_Id(id);
        genero.setVideojuegos(videojuegos);  // Asumiendo que tienes un setter en GenerosModel para esta lista
        
        return genero;
    }
}
