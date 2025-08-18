package com.project.habitflow.config;

import com.project.habitflow.entity.User;
import com.project.habitflow.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final UserRepository userRepository;
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public SecurityConfig(UserRepository userRepository, JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.userRepository = userRepository;
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Bean
//    UserDetailsService userDetailsService() {
//        return username -> userRepository.findByEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//    }
//
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    public AuthenticationEntryPoint authenticationEntryPoint() {
//        return (request, response, ex) -> {
//            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setContentType("application/json");
//            response.setHeader("WWW-Authenticate", "");
//            response.getWriter().write("{\"error\": \"Unauthorized access\"}");
//        };
//    }
//
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/docs/**").permitAll()
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//                )
//                .exceptionHandling(ex -> ex
//                        .authenticationEntryPoint(authenticationEntryPoint()))
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        // Aggiungi il filtro JWT prima del filtro standard
//        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//
////    @Bean
////    public CommandLineRunner initAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
////        return args -> {
////            String adminEmail = "admin@habitflow.com";
////            if (userRepository.findByEmail(adminEmail).isEmpty()) {
////                User admin = new User();
////                admin.setFirstName("Admin");
////                admin.setLastName("System");
////                admin.setEmail(adminEmail);
////                admin.setPassword(passwordEncoder.encode("admin123"));
////                admin.setRole(User.Role.ADMIN);
////                userRepository.save(admin);
////                System.out.println("Admin user created with email: " + adminEmail + " and password: admin123");
////            }
////        };
////    }
//
//}

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserRepository userRepository, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setHeader("WWW-Authenticate", "");
            response.getWriter().write("{\"error\": \"Unauthorized access\"}");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer ->
                configurer
                        .requestMatchers("/api/auth/**","/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**", "/docs").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
        );

        http.csrf(csrf -> csrf.disable());

        http.exceptionHandling(exceptionHandling ->
                exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint()));

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
