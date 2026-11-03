package com.campus.lostfound.controller;

import com.campus.lostfound.entity.Student;
import com.campus.lostfound.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository u, PasswordEncoder e) {
        this.users = u;
        this.encoder = e;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String create(@RequestParam("name") String name,
                         @RequestParam("email") String email,
                         @RequestParam("password") String password,
                         @RequestParam(name = "studentNumber", required = false) String studentNumber,
                         Model model,
                         RedirectAttributes ra) {
        if (password == null || password.length() < 8) {
            model.addAttribute("error", "Password must be at least 8 characters.");
            return "register";
        }
        if (users.existsByEmailIgnoreCase(email)) {
            model.addAttribute("error", "An account with this email already exists.");
            return "register";
        }
        users.save(new Student(name.trim(), email.trim().toLowerCase(), encoder.encode(password),
                studentNumber != null && !studentNumber.isBlank() ? studentNumber.trim() : null));
        ra.addFlashAttribute("success", "Account created. Please sign in.");
        return "redirect:/login";
    }
}
