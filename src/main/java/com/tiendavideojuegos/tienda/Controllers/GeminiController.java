package com.tiendavideojuegos.tienda.Controllers;

import com.tiendavideojuegos.tienda.Models.GeminiRequest;
import com.tiendavideojuegos.tienda.Services.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gamenest/gemini")
@CrossOrigin(origins = "http://localhost:3000")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody GeminiRequest request) {
        try {
            String response = geminiService.generateContent(request.getPrompt());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Verifica si es un error de cuota (429)
            if (e.getMessage().contains("Code 429")) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("Cuota de la API de Gemini excedida: " + e.getMessage());
            }
            // Otros errores
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la solicitud: " + e.getMessage());
        }
    }
}