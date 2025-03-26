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

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Videojuegos", description = "Controlador para gestionar videojuegos")
@RestController
@RequestMapping("/gamenest/videojuegos") 
public class VideojuegoController {

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
            throw new ResourceNotFoundException("No se encontraron videojuegos");
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

        return ResponseEntity.ok(videojuegos);
    }

    // Obtener un videojuego por ID
    @Operation(summary = "Obtener un videojuego por ID", description = "Devuelve los detalles de un videojuego específico")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @GetMapping("/id")
    public VideojuegoModel getVideojuegoById(@RequestParam Long id) throws ResourceNotFoundException {
        return videojuegoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un videojuego con el ID: " + id));
    }

    // Obtener un videojuego por título
    @Operation(summary = "Obtener un videojuego por título", description = "Devuelve los detalles de un videojuego específico por su título")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @GetMapping("/titulo")
    public VideojuegoModel getVideojuegoByTitulo(@RequestParam String titulo) throws ResourceNotFoundException {
        return videojuegoRepository.findByTitulo(titulo)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró un videojuego con el título: " + titulo));
    }

    // Obtener videojuegos por plataforma (usando ID de plataforma)
    @Operation(summary = "Obtener videojuegos por plataforma", description = "Devuelve una lista con todos los videojuegos filtrados por plataforma")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @GetMapping("/plataforma")
    public List<VideojuegoModel> getVideojuegosByPlataforma(@RequestParam Long plataformaId) throws ResourceNotFoundException {
        // Buscar los videojuegos que tengan esta plataforma
        List<VideojuegoModel> videojuegos = videojuegoRepository.findByPlataformas_Id(plataformaId); 
        if (videojuegos.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron videojuegos para la plataforma con ID: " + plataformaId);
        }
        return videojuegos;
    }

    // Obtener videojuegos por género (usando ID de género)
    @Operation(summary = "Obtener videojuegos por género", description = "Devuelve una lista de videojuegos filtrados por género")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @GetMapping("/generoId")
    public List<VideojuegoModel> getVideojuegosByGenero(@RequestParam Long generoId) throws ResourceNotFoundException {
        // Buscar los videojuegos que tengan este género
        List<VideojuegoModel> videojuegos = videojuegoRepository.findByGeneros_Id(generoId); 
        if (videojuegos.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron videojuegos para el género con ID: " + generoId);
        }
        return videojuegos;
    }

    // Obtener videojuegos por desarrollador
    @Operation(summary = "Obtener videojuegos por desarrollador", description = "Devuelve una lista de videojuegos filtrados por desarrollador")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @GetMapping("/desarrollador")
    public List<VideojuegoModel> getVideojuegosByDesarrollador(@RequestParam String desarrollador) throws ResourceNotFoundException {
        List<VideojuegoModel> videojuegos = videojuegoRepository.findByDesarrollador(desarrollador);
        if (videojuegos.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron videojuegos para este desarrollador: " + desarrollador);
        }
        return videojuegos;
    }

    // Obtener videojuegos por calificación de edad
    @Operation(summary = "Obtener videojuegos por calificación de edad", description = "Devuelve una lista de videojuegos filtrados por calificación de edad")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @GetMapping("/calificacionEdad")
    public List<VideojuegoModel> getVideojuegosByCalificacionEdad(@RequestParam String calificacionEdad) throws ResourceNotFoundException {
        List<VideojuegoModel> videojuegos = videojuegoRepository.findByCalificacionEdad(calificacionEdad);
        if (videojuegos.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron videojuegos para esta calificación de edad: " + calificacionEdad);
        }
        return videojuegos;
    }

    // Crear un nuevo videojuego
    @Operation(summary = "Crear un videojuego", description = "Añade un nuevo videojuego a la base de datos")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @PostMapping
    public VideojuegoModel createVideojuego(@RequestParam VideojuegoModel videojuego) throws ResourceNotFoundException {
        if (videojuego == null || videojuego.getPlataformas() == null || videojuego.getGeneros() == null) {
            throw new ResourceNotFoundException("Los datos del videojuego, plataformas o géneros son incorrectos");
        }

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
        return videojuegoRepository.save(videojuego);
    }

    // Actualizar un videojuego
    @Operation(summary = "Actualizar un videojuego", description = "Modifica un videojuego existente en la base de datos")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @PutMapping("/id")
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
            return videojuegoRepository.save(videojuego);
        }).orElseThrow(() -> new ResourceNotFoundException("No se encontró el videojuego con ID: " + id));
    }

    // Eliminar un videojuego
    @Operation(summary = "Eliminar un videojuego", description = "Elimina un videojuego por su ID")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @DeleteMapping("/id")
    public String deleteVideojuego(@PathVariable Long id) throws ResourceNotFoundException {
        if (videojuegoRepository.existsById(id)) {
            videojuegoRepository.deleteById(id);
            return "Videojuego eliminado correctamente";
        } else {
            throw new ResourceNotFoundException("Error: El videojuego con ID " + id + " no existe");
        }
    }
}
