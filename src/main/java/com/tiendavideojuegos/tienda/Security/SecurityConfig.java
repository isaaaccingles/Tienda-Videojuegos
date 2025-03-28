package com.tiendavideojuegos.tienda.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
            .httpBasic(Customizer.withDefaults());  // Habilita Basic Authentication
        
        System.out.println("Configuración de seguridad aplicada correctamente.");
    
        return http.build();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            System.out.println("Intentando autenticar usuario: " + username);
    
            UserModel user = userService.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("Usuario no encontrado: " + username);
                    return new UsernameNotFoundException("Usuario no encontrado");
                });
    
            System.out.println("Usuario encontrado: " + username + ", Role: " + user.getRole());
    
            return new User(user.getUsername(), user.getPassword(), 
                            AuthorityUtils.createAuthorityList("ROLE_" + user.getRole()));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }
}
