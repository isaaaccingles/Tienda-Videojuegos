package com.tiendavideojuegos.tienda.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiendavideojuegos.tienda.Services.GeminiService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping
    public Mono<String> chat(@RequestBody String prompt) {
        return geminiService.getResponse(prompt);
    }
    
}
