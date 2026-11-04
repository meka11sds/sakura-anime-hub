package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Регистрация нового пользователя
    public boolean registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            return false; // Пользователь с таким именем уже существует
        }

        User user = new User();
        user.setUsername(username);
        // Шифруем пароль перед сохранением в базу
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }
}
