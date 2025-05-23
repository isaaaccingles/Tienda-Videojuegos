package com.tiendavideojuegos.tienda.Services;

import com.tiendavideojuegos.tienda.Models.UserModel;
import com.tiendavideojuegos.tienda.Repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key; // Importación añadida
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class); // Logger

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String SECRET_KEY = "mi_clave_secreta_muy_segura_y_larga";
    private static final long EXPIRATION_TIME = 86400000L; // 24 horas

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserModel> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserModel> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserModel> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public UserModel registerUser(UserModel user) {
        if (user.getRole() == null) {
            user.setRole(UserModel.Role.USER); // Asignar el rol por defecto 'USER'
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean validatePassword(String rawPassword, String encryptedPassword) {
        return passwordEncoder.matches(rawPassword, encryptedPassword);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
    }

    @SuppressWarnings("deprecation")
    public String generateToken(UserModel user) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        Date issuedAt = new Date();
        Date expiration = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        logger.info("Issued At: {}", issuedAt);
        logger.info("Expiration: {}", expiration);
        logger.info("User found: {}, Role: {}", user.getUsername(), user.getRole());

        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        logger.info("Authorities: {}", authorities);

        String userId = user.getId().toString();
        logger.info("Generando token para userId: {}", userId);

        return Jwts.builder()
            .setSubject(userId) // Usar el ID del usuario como subject
            .setIssuedAt(issuedAt)
            .setExpiration(expiration)
            .claim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }
}
