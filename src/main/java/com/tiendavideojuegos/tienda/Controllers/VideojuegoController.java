package com.tiendavideojuegos.tienda.Controllers;

import com.tiendavideojuegos.tienda.Exceptions.ResourceNotFoundException;
import com.tiendavideojuegos.tienda.Models.GenerosModel;
import com.tiendavideojuegos.tienda.Models.PlataformaModel;
import com.tiendavideojuegos.tienda.Models.VideojuegoModel;
import com.tiendavideojuegos.tienda.Repositories.GenerosRepository;
import com.tiendavideojuegos.tienda.Repositories.PlataformaRepository;
import com.tiendavideojuegos.tienda.Repositories.VideojuegoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper; 

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(name = "Videojuegos", description = "Controlador para gestionar videojuegos")
@RestController
@CrossOrigin(origins = "http://localhost:3001") 
@RequestMapping("/gamenest/videojuegos") 
public class VideojuegoController {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(VideojuegoController.class);

    @Autowired
    private ObjectMapper objectMapper;  

    @Autowired
    private VideojuegoRepository videojuegoRepository;
    @Autowired
    private PlataformaRepository plataformaRepository; 
    @Autowired
    private GenerosRepository generoRepository;

    // Obtener todos los videojuegos
    @Operation(summary = "Obtener todos los videojuegos", description = "Devuelve una lista con todos los videojuegos registrados")
    @GetMapping
    public List<VideojuegoModel> getAllVideojuegos() throws ResourceNotFoundException {
        List<VideojuegoModel> videojuegos = videojuegoRepository.findAll();
        if (videojuegos.isEmpty()) {
            logger.warn("No se encontraron videojuegos");
            throw new ResourceNotFoundException("No se encontraron videojuegos");
        }

        try {
            // Serializamos la lista a JSON para el log
            String videojuegosJson = objectMapper.writeValueAsString(videojuegos);
            logger.info("Se encontraron los siguientes videojuegos {}", videojuegosJson); // Log con el JSON
        } catch (Exception e) {
            logger.error("Error al serializar los videojuegos a JSON", e);
        }

        return videojuegos;
    }

    @GetMapping("/filtrar")
    public ResponseEntity<?> filtrarVideojuegos(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String desarrollador,
            @RequestParam(required = false) String calificacionEdad,
            @RequestParam(required = false) Long plataformaId,
            @RequestParam(required = false) Long generoId) {

        List<VideojuegoModel> videojuegos = videojuegoRepository.findAll();

        if (titulo != null && !titulo.isEmpty()) {
            videojuegos.retainAll(videojuegoRepository.findByTitulo(titulo).stream().toList());
        }
        if (desarrollador != null && !desarrollador.isEmpty()) {
            videojuegos.retainAll(videojuegoRepository.findByDesarrollador(desarrollador));
        }
        if (calificacionEdad != null && !calificacionEdad.isEmpty()) {
            videojuegos.retainAll(videojuegoRepository.findByCalificacionEdad(calificacionEdad));
        }
        if (plataformaId != null) {
            videojuegos.retainAll(videojuegoRepository.findByPlataformas_Id(plataformaId));
        }
        if (generoId != null) {
            videojuegos.retainAll(videojuegoRepository.findByGeneros_Id(generoId));
        }

        if (videojuegos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontraron videojuegos con los criterios especificados.");
        }

        try {
            // Serializamos la lista filtrada a JSON para el log
            String videojuegosJson = objectMapper.writeValueAsString(videojuegos);
            logger.info("Videojuegos filtrados: {}", videojuegosJson); // Log con el JSON
        } catch (Exception e) {
            logger.error("Error al serializar los videojuegos filtrados a JSON", e);
        }

        return ResponseEntity.ok(videojuegos);
    }


    // Obtener un videojuego por ID
    @Operation(summary = "Obtener un videojuego por ID", description = "Devuelve los detalles de un videojuego específico")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @GetMapping("/{id}")
    public VideojuegoModel getVideojuegoById(@PathVariable Long id) throws ResourceNotFoundException {
        // Intentar encontrar el videojuego
        VideojuegoModel videojuego = videojuegoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un videojuego con el ID: " + id));

        try {
            // Serializamos el videojuego a JSON para el log
            String videojuegoJson = objectMapper.writeValueAsString(videojuego);
            // Logueamos el videojuego encontrado
            logger.info("Se encontró el videojuego con ID {}: {}", id, videojuegoJson);  // Log con el JSON
        } catch (Exception e) {
            logger.error("Error al serializar el videojuego con ID {} a JSON", id, e);
        }

        // Retornar el videojuego encontrado
        return videojuego;
    }

    // Obtener un videojuego por título
    @Operation(summary = "Obtener un videojuego por título", description = "Devuelve los detalles de un videojuego específico por su título")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @GetMapping("/titulo")
    public VideojuegoModel getVideojuegoByTitulo(@RequestParam String titulo) throws ResourceNotFoundException, JsonProcessingException {
        VideojuegoModel videojuego = videojuegoRepository.findByTitulo(titulo)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un videojuego con el título: " + titulo));
        
        // Serializamos el videojuego a JSON para los logs
        String videojuegoJson = objectMapper.writeValueAsString(videojuego);
        
        // Log con el JSON del videojuego encontrado
        logger.info("Se encontró el siguiente videojuego {}", videojuegoJson);
        
        return videojuego;
    }

    // Obtener videojuegos por plataforma (usando ID de plataforma)
    @Operation(summary = "Obtener videojuegos por plataforma", description = "Devuelve una lista con todos los videojuegos filtrados por plataforma")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @GetMapping("/plataforma")
    public List<VideojuegoModel> getVideojuegosByPlataforma(@RequestParam Long plataformaId) throws ResourceNotFoundException {
        try {
            // Buscar los videojuegos que tengan esta plataforma
            List<VideojuegoModel> videojuegos = videojuegoRepository.findByPlataformas_Id(plataformaId);
            
            if (videojuegos.isEmpty()) {
                throw new ResourceNotFoundException("No se encontraron videojuegos para la plataforma con ID: " + plataformaId);
            }

            // Serializamos la lista a JSON para el log
            String videojuegosJson = objectMapper.writeValueAsString(videojuegos);
            
            // Log con el JSON de los videojuegos encontrados
            logger.info("Se encontraron los siguientes videojuegos para la plataforma con ID {}: {}", plataformaId, videojuegosJson);
            
            return videojuegos;
        } catch (JsonProcessingException e) {
            logger.error("Error al serializar los videojuegos para la plataforma con ID: {}", plataformaId, e);
            throw new ResourceNotFoundException("Error al procesar los videojuegos para la plataforma con ID: " + plataformaId);
        } catch (Exception e) {
            logger.error("Error al obtener los videojuegos para la plataforma con ID: {}", plataformaId, e);
            throw e;
        }
    }

    // Obtener videojuegos por género (usando ID de género)
    @Operation(summary = "Obtener videojuegos por género", description = "Devuelve una lista de videojuegos filtrados por género")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @GetMapping("/generoId")
    public List<VideojuegoModel> getVideojuegosByGenero(@RequestParam Long generoId) throws ResourceNotFoundException {
        try {
            // Buscar los videojuegos que tengan este género
            List<VideojuegoModel> videojuegos = videojuegoRepository.findByGeneros_Id(generoId);
            
            if (videojuegos.isEmpty()) {
                throw new ResourceNotFoundException("No se encontraron videojuegos para el género con ID: " + generoId);
            }

            // Serializamos la lista a JSON para el log
            String videojuegosJson = objectMapper.writeValueAsString(videojuegos);
            
            // Log con el JSON de los videojuegos encontrados
            logger.info("Se encontraron los siguientes videojuegos para el género con ID {}: {}", generoId, videojuegosJson);
            
            return videojuegos;
        } catch (JsonProcessingException e) {
            logger.error("Error al serializar los videojuegos para el género con ID: {}", generoId, e);
            throw new ResourceNotFoundException("Error al procesar los videojuegos para el género con ID: " + generoId);
        } catch (Exception e) {
            logger.error("Error al obtener los videojuegos para el género con ID: {}", generoId, e);
            throw e;
        }
    }

    // Obtener videojuegos por desarrollador
    @Operation(summary = "Obtener videojuegos por desarrollador", description = "Devuelve una lista de videojuegos filtrados por desarrollador")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @GetMapping("/desarrollador")
    public List<VideojuegoModel> getVideojuegosByDesarrollador(@RequestParam String desarrollador) throws ResourceNotFoundException {
        try {
            // Buscar los videojuegos que tengan este desarrollador
            List<VideojuegoModel> videojuegos = videojuegoRepository.findByDesarrollador(desarrollador);
            
            if (videojuegos.isEmpty()) {
                throw new ResourceNotFoundException("No se encontraron videojuegos para este desarrollador: " + desarrollador);
            }

            // Serializamos la lista a JSON para el log
            String videojuegosJson = objectMapper.writeValueAsString(videojuegos);
            
            // Log con el JSON de los videojuegos encontrados
            logger.info("Se encontraron los siguientes videojuegos para el desarrollador {}: {}", desarrollador, videojuegosJson);
            
            return videojuegos;
        } catch (JsonProcessingException e) {
            logger.error("Error al serializar los videojuegos para el desarrollador: {}", desarrollador, e);
            throw new ResourceNotFoundException("Error al procesar los videojuegos para el desarrollador: " + desarrollador);
        } catch (Exception e) {
            logger.error("Error al obtener los videojuegos para el desarrollador: {}", desarrollador, e);
            throw e;
        }
    }

    // Obtener videojuegos por calificación de edad
    @Operation(summary = "Obtener videojuegos por calificación de edad", description = "Devuelve una lista de videojuegos filtrados por calificación de edad")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @GetMapping("/calificacionEdad")
    public List<VideojuegoModel> getVideojuegosByCalificacionEdad(@RequestParam String calificacionEdad) throws ResourceNotFoundException {
        try {
            // Buscar los videojuegos que tengan esta calificación de edad
            List<VideojuegoModel> videojuegos = videojuegoRepository.findByCalificacionEdad(calificacionEdad);
            
            if (videojuegos.isEmpty()) {
                throw new ResourceNotFoundException("No se encontraron videojuegos para esta calificación de edad: " + calificacionEdad);
            }

            // Serializamos la lista a JSON para el log
            String videojuegosJson = objectMapper.writeValueAsString(videojuegos);
            
            // Log con el JSON de los videojuegos encontrados
            logger.info("Se encontraron los siguientes videojuegos para la calificación de edad {}: {}", calificacionEdad, videojuegosJson);
            
            return videojuegos;
        } catch (JsonProcessingException e) {
            logger.error("Error al serializar los videojuegos para la calificación de edad: {}", calificacionEdad, e);
            throw new ResourceNotFoundException("Error al procesar los videojuegos para la calificación de edad: " + calificacionEdad);
        } catch (Exception e) {
            logger.error("Error al obtener los videojuegos para la calificación de edad: {}", calificacionEdad, e);
            throw e;
        }
    }

    // Crear un nuevo videojuego
    @Operation(summary = "Crear un videojuego", description = "Añade un nuevo videojuego a la base de datos")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @PostMapping
    public VideojuegoModel createVideojuego(@RequestParam VideojuegoModel videojuego) throws ResourceNotFoundException {
        try {
            // Validar que el videojuego, plataformas o géneros sean correctos
            if (videojuego == null || videojuego.getPlataformas() == null || videojuego.getGeneros() == null) {
                throw new ResourceNotFoundException("Los datos del videojuego, plataformas o géneros son incorrectos");
            }

            // Log de intento de creación del videojuego
            logger.info("Intentando crear un nuevo videojuego con el título: {}", videojuego.getTitulo());

            // Buscar las plataformas por sus ID
            List<PlataformaModel> plataformas = new ArrayList<>();
            for (Long plataformaId : videojuego.getPlataformasIds()) {  
                PlataformaModel plataformaExistente = plataformaRepository.findById(plataformaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Plataforma no encontrada con ID: " + plataformaId));
                plataformas.add(plataformaExistente);  
            }
            videojuego.setPlataformas(plataformas); 

            // Buscar los géneros por sus ID
            List<GenerosModel> generos = new ArrayList<>();
            for (Long generoId : videojuego.getGenerosIds()) {  
                GenerosModel generoExistente = generoRepository.findById(generoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + generoId));
                generos.add(generoExistente);  
            }
            videojuego.setGeneros(generos); 

            // Guardar el videojuego con las plataformas y géneros asociados
            VideojuegoModel savedVideojuego = videojuegoRepository.save(videojuego);

            // Log de éxito con los detalles del videojuego creado
            String videojuegoJson = objectMapper.writeValueAsString(savedVideojuego);
            logger.info("Nuevo videojuego creado con éxito: {}", videojuegoJson);

            return savedVideojuego;
        } catch (JsonProcessingException e) {
            logger.error("Error al serializar el videojuego a JSON durante la creación", e);
            throw new ResourceNotFoundException("Error al procesar los datos del videojuego");
        } catch (ResourceNotFoundException e) {
            logger.error("Error al crear el videojuego: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error desconocido al crear el videojuego", e);
            throw e;
        }
    }

    // Actualizar un videojuego
    @Operation(summary = "Actualizar un videojuego", description = "Modifica un videojuego existente en la base de datos")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @PutMapping("/{id}")
    public VideojuegoModel updateVideojuego(@PathVariable Long id, @RequestBody VideojuegoModel videojuegoDetails) throws ResourceNotFoundException {
        return videojuegoRepository.findById(id).map(videojuego -> {
            videojuego.setTitulo(videojuegoDetails.getTitulo());
            videojuego.setDescripcion(videojuegoDetails.getDescripcion());
            videojuego.setGeneros(videojuegoDetails.getGeneros());
            videojuego.setPrecio(videojuegoDetails.getPrecio());
            videojuego.setStock(videojuegoDetails.getStock());
            videojuego.setFechaLanzamiento(videojuegoDetails.getFechaLanzamiento());
            videojuego.setDesarrollador(videojuegoDetails.getDesarrollador());
            videojuego.setEditor(videojuegoDetails.getEditor());
            videojuego.setCalificacionEdad(videojuegoDetails.getCalificacionEdad());
            videojuego.setPlataformas(videojuegoDetails.getPlataformas());
            
            // Log: Videojuego actualizado
            logger.info("Videojuego con ID: {} actualizado exitosamente", id);
            return videojuegoRepository.save(videojuego);
        }).orElseThrow(() -> {
            // Log: No encontrado
            logger.error("No se encontró el videojuego con ID: {}", id);
            return new ResourceNotFoundException("No se encontró el videojuego con ID: " + id);
        });
    }

    @Operation(summary = "Eliminar un videojuego", description = "Elimina un videojuego por su ID")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @DeleteMapping("/{id}")
    public String deleteVideojuego(@PathVariable Long id) throws ResourceNotFoundException {
        if (videojuegoRepository.existsById(id)) {
            videojuegoRepository.deleteById(id);
            // Log: Videojuego eliminado
            logger.info("Videojuego con ID: {} eliminado correctamente", id);
            return "Videojuego eliminado correctamente";
        } else {
            // Log: No encontrado
            logger.error("Error: El videojuego con ID: {} no existe", id);
            throw new ResourceNotFoundException("Error: El videojuego con ID " + id + " no existe");
        }
    }    
}
