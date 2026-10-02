package com.vansh.resume_screening;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    private final ApplicantUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(
            ApplicantUserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        username = username == null ? "" : username.trim();
        fullName = fullName == null ? "" : fullName.trim();
        email = email == null ? "" : email.trim().toLowerCase();
        phone = phone == null ? "" : phone.trim();

        if (username.isBlank() || fullName.isBlank() || email.isBlank() || phone.isBlank() || password == null || password.isBlank()) {
            model.addAttribute("error", "Please fill all required fields.");
            return "register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "register";
        }

        if (password.length() < 6) {
            model.addAttribute("error", "Password must contain at least 6 characters.");
            return "register";
        }

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            model.addAttribute("error", "Username is already registered.");
            return "register";
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            model.addAttribute("error", "Email is already registered.");
            return "register";
        }

        ApplicantUser user = new ApplicantUser();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        // Registration can ONLY create candidate accounts.
        user.setRole("CANDIDATE");

        userRepository.save(user);

        return "redirect:/login?registered=true";
    }
}
