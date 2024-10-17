package com.andrewsalygin.authorizationservice.service;

import com.andrewsalygin.authorizationservice.domain.entity.UserEntity;
import com.andrewsalygin.authorizationservice.domain.model.User;
import com.andrewsalygin.authorizationservice.exception.UserAlreadyExistsException;
import com.andrewsalygin.authorizationservice.repository.UserRepository;
import com.andrewsalygin.authorizationservice.util.SaltGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public void registerUser(String username, String password) throws UserAlreadyExistsException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException(
                    "Ошибка регистрации",
                    "Пользователь " + username + " уже зарегистрирован.",
                    HttpStatus.BAD_REQUEST
            );
        }

        SaltGenerator saltGenerator = new SaltGenerator();

        String salt = saltGenerator.generateSalt();
        String hashedPassword = passwordEncoder.encode(password + salt);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPasswordHash(hashedPassword);
        userEntity.setSalt(salt);

        userRepository.save(userEntity);
    }

    public User authenticateUser(String username, String password) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new BadCredentialsException("Неправильный пароль или логин.")
        );

        String salt = userEntity.getSalt();

        if (!passwordEncoder.matches(password + salt, userEntity.getPasswordHash())) {
            throw new BadCredentialsException("Неправильный пароль или логин.");
        }

        return new User(userEntity.getUuid(), userEntity.getUsername());
    }
}

