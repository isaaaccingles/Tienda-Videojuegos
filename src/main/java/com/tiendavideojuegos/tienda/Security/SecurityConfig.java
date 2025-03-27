package com.tiendavideojuegos.tienda.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;

import com.tiendavideojuegos.tienda.Services.UserService;
import com.tiendavideojuegos.tienda.Models.UserModel;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;  // Servicio que carga los usuarios

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Deshabilitar CSRF para pruebas con Postman
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/gamenest/users/register", "/gamenest/users/login").permitAll()  // Estos pueden ser públicos
                .requestMatchers("/gamenest/users/delete/**", "/gamenest/users/update/**").hasRole("ADMIN")  // Solo ADMIN
                .anyRequest().authenticated()  // Requiere autenticación en otras solicitudes
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // No usar sesión
            .httpBasic(Customizer.withDefaults());  // Habilita Basic Authentication de forma nueva
    
        return http.build();
    }
    


    // UserDetailsService: Cargar usuarios por nombre de usuario para autenticación
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            UserModel user = userService.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("Usuario no encontrado");
            }

            // Devuelve un UserDetails con las credenciales y roles del usuario
            return new User(user.getUsername(), user.getPassword(), 
                            AuthorityUtils.createAuthorityList("ROLE_" + user.getRole()));
        };
    }
}
