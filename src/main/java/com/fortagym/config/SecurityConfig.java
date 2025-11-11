package com.fortagym.config;

import com.fortagym.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationSuccessHandler successHandler;

    /*@Autowired*/
    
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          AuthenticationSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/registro", "/login", "/css/**", "/js/**", "/img/**","/nutricion/usuarios","/nutricion/nuevo/{id}", "/rutina/usuarios","/rutina/nueva/{id}",
            "/rutina/guardar","/cartilla/{id}", "/resumenCompra/{id}","/resumenPase/{id}").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/rutina/**").hasRole("ENTRENADOR")
            .requestMatchers("/nutricion/**").hasRole("NUTRICIONISTA")
            .anyRequest().authenticated()
        )
        .formLogin(login -> login
            .loginPage("/login")
            .successHandler(successHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        );

        return http.build();
    }
}