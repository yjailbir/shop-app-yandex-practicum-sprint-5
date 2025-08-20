package ru.yjailbir.shopappyandexpracticumsprint5.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.UserCrudRepository;

@Service
public class ShopUserDetailsService implements ReactiveUserDetailsService {
    private final UserCrudRepository repository;

    public ShopUserDetailsService(UserCrudRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return repository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")))
                .map(ShopUserDetails::new);
    }
}
