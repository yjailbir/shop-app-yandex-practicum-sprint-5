package ru.yjailbir.shopappyandexpracticumsprint5.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveAuthenticationManager authManager,
            ServerSecurityContextRepository contextRepository
    ) {
        return http
                .securityContextRepository(contextRepository)
                .authenticationManager(authManager)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login", "/register", "/shop", "/shop/products/**", "/add-products").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .authenticationSuccessHandler((webFilterExchange, authentication) ->
                                reactor.core.publisher.Mono.defer(() -> {
                                    var resp = webFilterExchange.getExchange().getResponse();
                                    resp.setStatusCode(org.springframework.http.HttpStatus.FOUND);
                                    resp.getHeaders().setLocation(java.net.URI.create("/shop"));
                                    return resp.setComplete();
                                }))
                        .authenticationFailureHandler((webFilterExchange, ex) ->
                                reactor.core.publisher.Mono.defer(() -> {
                                    var resp = webFilterExchange.getExchange().getResponse();
                                    resp.setStatusCode(org.springframework.http.HttpStatus.FOUND);
                                    resp.getHeaders().setLocation(java.net.URI.create("/login?error"));
                                    return resp.setComplete();
                                }))
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((exchange, authentication) ->
                                exchange.getExchange().getSession()
                                        .flatMap(WebSession::invalidate)
                                        .then(Mono.fromRunnable(() -> {
                                            exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                                            exchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/shop"));
                                        }))
                        )
                ).build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        UserDetailsRepositoryReactiveAuthenticationManager am =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        am.setPasswordEncoder(passwordEncoder);
        return am;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }
}
