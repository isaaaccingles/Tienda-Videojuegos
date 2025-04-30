package com.tiendavideojuegos.tienda.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import com.tiendavideojuegos.tienda.Models.UserModel;
import com.tiendavideojuegos.tienda.Services.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Públicos
                .requestMatchers("/gamenest/users/register", "/gamenest/users/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/gamenest/videojuegos", "/gamenest/videojuegos/**").permitAll()
                .requestMatchers("/gamenest/carrito/agregar").permitAll()
                // Permite el acceso a las imágenes (ajusta la ruta de las imágenes)
                .requestMatchers("/images/**").permitAll() // Permite acceder a imágenes sin autenticación
                .requestMatchers("/gamenest/carrito/agregar").permitAll()
                // Accesibles por usuarios autenticados
                // .requestMatchers("/gamenest/carrito/**").authenticated()
                // Solo ADMIN
                .requestMatchers("/gamenest/users/delete/**", "/gamenest/users/update/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/gamenest/videojuegos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/gamenest/videojuegos/id/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/gamenest/videojuegos/id/**").hasRole("ADMIN")
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            UserModel user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                AuthorityUtils.createAuthorityList("ROLE_" + user.getRole())
            );
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
