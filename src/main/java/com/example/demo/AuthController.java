
package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerProcess(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        if (username == null ||
                !username.matches("[a-zA-Z0-9_]{3,30}")) {
            model.addAttribute("error",
                    "Логин должен содержать от 3 до 30 символов: латинские буквы, цифры или _");
            return "register";
        }

        if (password == null || password.length() < 8) {
            model.addAttribute("error",
                    "Пароль должен содержать минимум 8 символов!");
            return "register";
        }

        boolean success = userService.registerUser(username, password);

        if (!success) {
            model.addAttribute("error",
                    "Пользователь с таким именем уже существует!");
            return "register";
        }

        return "redirect:/login?registered";
    }
}

