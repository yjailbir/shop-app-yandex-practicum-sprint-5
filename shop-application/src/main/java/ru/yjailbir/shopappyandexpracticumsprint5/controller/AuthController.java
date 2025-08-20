package ru.yjailbir.shopappyandexpracticumsprint5.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yjailbir.shopappyandexpracticumsprint5.dto.RegistrationDto;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.UserEntity;
import ru.yjailbir.shopappyandexpracticumsprint5.repository.UserCrudRepository;

@Controller
public class AuthController {
    private final UserCrudRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserCrudRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    @ResponseBody
    public Mono<String> register(@ModelAttribute RegistrationDto dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Mono.just("username/password must not be empty");
        }

        return userRepository.existsByUsername(username)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.just("username already taken");
                    }
                    var user = new UserEntity(username, passwordEncoder.encode(password));
                    return userRepository.save(user).then(Mono.just("ok"));
                });
    }
}
