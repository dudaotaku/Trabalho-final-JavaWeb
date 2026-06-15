package com.trabalhoFinal.JavaWeb.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UsuarioDetailService userDetailService;

    public SecurityConfig(UsuarioDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/eventos").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/eventos/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/eventos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/eventos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/eventos/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/participantes").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/participantes/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/participantes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/participantes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/participantes/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/inscricoes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/incricoes/eventos/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/incricoes/listar").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/usuario").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/usuario").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/usuario/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/usuario/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/usuario/**").hasRole("ADMIN")

                        .anyRequest()
                        .authenticated())
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"erro\": \"Acesso negado. Você não tem permissão para acessar este recurso.\"}");
                        })
                )
                .userDetailsService(userDetailService)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:5500"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
