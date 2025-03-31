package com.tiendavideojuegos.tienda.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;

import com.tiendavideojuegos.tienda.Services.UserService;

import org.springframework.http.HttpMethod;

import com.tiendavideojuegos.tienda.Models.UserModel;

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
                // Endpoints públicos (sin autenticación)
                .requestMatchers("/gamenest/users/register", "/gamenest/users/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/gamenest/videojuegos", "/gamenest/videojuegos/**").permitAll() // Todos los GET de videojuegos públicos

                // Endpoints restringidos a ROLE_ADMIN
                .requestMatchers("/gamenest/users/delete/**", "/gamenest/users/update/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/gamenest/videojuegos").hasRole("ADMIN") // Crear videojuego
                .requestMatchers(HttpMethod.PUT, "/gamenest/videojuegos/id/**").hasRole("ADMIN") // Actualizar videojuego
                .requestMatchers(HttpMethod.DELETE, "/gamenest/videojuegos/id/**").hasRole("ADMIN") // Eliminar videojuego

                // Cualquier otra solicitud requiere autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // @Bean
    // public JWTAuthenticationFilter jwtAuthenticationFilter() {
    //     return new JWTAuthenticationFilter();
    // }



    // // @Bean
    // // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // //     http
    // //         .csrf(csrf -> csrf.disable())
    // //         .authorizeHttpRequests(auth -> auth
    // //             .requestMatchers("/gamenest/users/register", "/gamenest/users/login").permitAll()
    // //             .requestMatchers("/gamenest/users/delete/**", "/gamenest/users/update/**").hasRole("ADMIN")
    // //             .anyRequest().authenticated()
    // //         )
    // //         .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    // //         .httpBasic(Customizer.withDefaults())
    // //         .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // Usamos el bean directamente
        
    // //     return http.build();
    // // }

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