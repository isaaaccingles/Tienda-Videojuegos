package com.tiendavideojuegos.tienda.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tiendavideojuegos.tienda.Models.VideojuegoModel;
import com.tiendavideojuegos.tienda.Repositories.VideojuegoRepository;
import com.tiendavideojuegos.tienda.Models.GenerosModel;
import com.tiendavideojuegos.tienda.Models.PlataformaModel;
import com.tiendavideojuegos.tienda.Repositories.PlataformaRepository;
import com.tiendavideojuegos.tienda.Repositories.GenerosRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VideojuegoService {

    @Autowired
    private VideojuegoRepository videojuegoRepository;

    @Autowired
    private PlataformaRepository plataformaRepository;

    @Autowired
    private GenerosRepository generosRepository;

    // Obtener todos los videojuegos
    public List<VideojuegoModel> getAllVideojuegos() {
        return videojuegoRepository.findAll();
    }

    // Guardar un nuevo videojuego
    public VideojuegoModel saveVideojuego(VideojuegoModel videojuego) {
        asignarPlataformasYGeneros(videojuego);
        return videojuegoRepository.save(videojuego);
    }

    // Obtener un videojuego por ID
    public Optional<VideojuegoModel> getVideojuegoById(Long id) {
        return videojuegoRepository.findById(id);
    }

    // Actualizar un videojuego por ID
    public ResponseEntity<VideojuegoModel> updateVideojuegoById(VideojuegoModel updatedVideojuego, Long id) {
        Optional<VideojuegoModel> optionalVideojuego = videojuegoRepository.findById(id);

        if (optionalVideojuego.isPresent()) {
            VideojuegoModel existingVideojuego = optionalVideojuego.get();

            // Actualizar atributos del videojuego
            existingVideojuego.setTitulo(updatedVideojuego.getTitulo());
            existingVideojuego.setDescripcion(updatedVideojuego.getDescripcion());
            existingVideojuego.setPrecio(updatedVideojuego.getPrecio());
            existingVideojuego.setStock(updatedVideojuego.getStock());
            existingVideojuego.setFechaLanzamiento(updatedVideojuego.getFechaLanzamiento());
            existingVideojuego.setDesarrollador(updatedVideojuego.getDesarrollador());
            existingVideojuego.setEditor(updatedVideojuego.getEditor());
            existingVideojuego.setCalificacionEdad(updatedVideojuego.getCalificacionEdad());

            // Asignar plataformas y géneros usando los IDs al objeto existente
            asignarPlataformasYGeneros(existingVideojuego);

            // Guardar el videojuego actualizado
            VideojuegoModel videojuegoGuardado = videojuegoRepository.save(existingVideojuego);
            
            return ResponseEntity.ok(videojuegoGuardado);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Eliminar un videojuego por ID
    public ResponseEntity<Void> deleteVideojuego(Long id) {
        if (!videojuegoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        videojuegoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Buscar videojuegos por plataforma
    public List<VideojuegoModel> getVideojuegosByPlataforma(Long plataformaId) {
        return videojuegoRepository.findByPlataformas_Id(plataformaId);
    }

    // Buscar videojuegos por género
    public List<VideojuegoModel> getVideojuegosByGenero(Long generoId) {
        return videojuegoRepository.findByGeneros_Id(generoId);
    }

    // Asignar plataformas y géneros a un videojuego
    private void asignarPlataformasYGeneros(VideojuegoModel videojuego) {
        if (videojuego.getPlataformasIds() != null) {
            List<PlataformaModel> plataformas = new ArrayList<>();
            for (Long plataformaId : videojuego.getPlataformasIds()) {
                plataformaRepository.findById(plataformaId)
                    .ifPresent(plataformas::add); // Solo agregamos si la plataforma existe
            }
            videojuego.setPlataformas(plataformas);
        }

        if (videojuego.getGenerosIds() != null) {
            List<GenerosModel> generos = new ArrayList<>();
            for (Long generoId : videojuego.getGenerosIds()) {
                generosRepository.findById(generoId)
                    .ifPresent(generos::add); // Solo agregamos si el género existe
            }
            videojuego.setGeneros(generos);
        }
    }
}
