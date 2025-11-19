package com.politechnika_warszawska.wyszukiwarkaprac.config;


import com.politechnika_warszawska.wyszukiwarkaprac.services.UzytkownikService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    //to działa poprawnie
    private final UzytkownikService uzytkownikService;
    private final JwtAuthFilter jwtAuthFilter;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UzytkownikService uzytkownikService, JwtAuthFilter jwtAuthFilter, PasswordEncoder passwordEncoder) {
        this.uzytkownikService = uzytkownikService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.passwordEncoder = passwordEncoder;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // korzystamy z JWT nie z ciasteczek
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authz -> authz
                        // kazdy moze sie logowac nie bedzie specjalnych rol dla bd, tez dopuszczamy kazdego tylko gdy bedziemy chceli polubic jakies oferty to bez jwt nie mozna, bedzie wymagane
                        .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/oferty/**").permitAll()
                .anyRequest().authenticated()
                        // sorubg nie bedzie tworzyc seshu dka /api/**
                ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // dodajemy filtr JWT dla API
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // wymagane przez h2 na naglowki
                .headers(headers -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()))
                .formLogin(withDefaults());


        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    // jak szukac uzytkownikow i sprawdzac hasel
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(uzytkownikService); // uzyj serwisu do szukania w bazie
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder);// do porowynwania hasel
        return daoAuthenticationProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // adres angulara
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        //naglowki dla JWT
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        // wysylanie credentials
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // konfig na wszystkie adresy URL w aplikacji
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
// wersja do wchodzenia i sprawdzania h2-console na dole na gorze ta poprawna w razie co zmienic authenitcated na permit all



//package com.politechnika_warszawska.wyszukiwarkaprac.config; // Upewnij się, że pakiet jest OK
//
//import com.politechnika_warszawska.wyszukiwarkaprac.services.UzytkownikService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.cors.CorsConfigurationSource;
//
//import java.util.List;
//
//import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console; // WAŻNY IMPORT
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final UzytkownikService uzytkownikService;
//    private final JwtAuthFilter jwtAuthFilter;
//    private final PasswordEncoder passwordEncoder;
//
//    public SecurityConfig(UzytkownikService uzytkownikService, JwtAuthFilter jwtAuthFilter, PasswordEncoder passwordEncoder) {
//        this.uzytkownikService = uzytkownikService;
//        this.jwtAuthFilter = jwtAuthFilter;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    // --- ŁAŃCUCH 1: Dla API (JWT, Bezstanowy) ---
//    // (Ma wyższy priorytet @Order(1))
//    @Bean
//    @Order(1)
//    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // Ten łańcuch dotyczy TYLKO ścieżek /api/**
//                .securityMatcher(new AntPathRequestMatcher("/api/**"))
//
//                .csrf(AbstractHttpConfigurer::disable) // Wyłącz CSRF dla API
//                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Włącz CORS
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/api/auth/**").permitAll()    // Logowanie/rejestracja publiczne
//                        .requestMatchers("/api/oferty/**").permitAll() // Wyszukiwarka publiczna
//                        .anyRequest().authenticated()                   // Reszta /api wymaga tokena
//                )
//                // KLUCZOWE: API jest bezstanowe
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                // Dodaj nasz filtr JWT
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    // --- ŁAŃCUCH 2: Dla reszty (H2 Console, Stanowy) ---
//    // (Ma niższy priorytet @Order(2) i łapie wszystko inne)
//    @Bean
//    @Order(2)
//    public SecurityFilterChain formLoginSecurityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // Ten łańcuch dotyczy WSZYSTKIEGO INNEGO (np. /h2-console)
//                .securityMatcher(new AntPathRequestMatcher("/**"))
//
//                .authorizeHttpRequests(authz -> authz
//                        // Zezwól na żądania DO konsoli H2 (aby działała poprawnie)
//                        .requestMatchers(toH2Console()).permitAll()
//                        // Cała reszta (np. wejście na /) wymaga zalogowania
//                        .anyRequest().authenticated()
//                )
//                // KLUCZOWE: Użyj domyślnej sesji (stanowej), aby formLogin działał
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//
//                // Włącz logowanie formularzem
//                .formLogin(withDefaults())
//
//                // Ustawienia dla H2
//                .csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console())) // Wyłącz CSRF tylko dla H2
//                .headers(headers -> headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()));
//
//        return http.build();
//    }
//
//    // --- Wspólne Beany (używane przez oba łańcuchy) ---
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setUserDetailsService(uzytkownikService);
//        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder);
//        return daoAuthenticationProvider;
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}