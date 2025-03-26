package com.tiendavideojuegos.tienda.Controllers;

import com.tiendavideojuegos.tienda.Models.PlataformaModel;
import com.tiendavideojuegos.tienda.Repositories.PlataformaRepository;
import com.tiendavideojuegos.tienda.Exceptions.ResourceNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Plataformas", description = "Controlador para gestionar plataformas de videojuegos")
@RestController
@RequestMapping("/gamenest/plataformas")
public class PlataformasController {

    @Autowired
    private PlataformaRepository plataformaRepository;

    // Obtener todas las plataformas
    @Operation(summary = "Obtener todas las plataformas", description = "Devuelve una lista con todas las plataformas disponibles")
    @GetMapping
    public List<PlataformaModel> getAllPlataformas() throws ResourceNotFoundException {
        List<PlataformaModel> plataformas = plataformaRepository.findAll();
        if (plataformas.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron plataformas");
        }
        return plataformas;
    }

    // Obtener una plataforma por ID
    @Operation(summary = "Obtener una plataforma por ID", description = "Devuelve los detalles de una plataforma específica")
    @GetMapping("/id")
    public PlataformaModel getPlataformaById(@RequestParam Long id) throws ResourceNotFoundException {
        return plataformaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró una plataforma con el ID: " + id));
    }
}
