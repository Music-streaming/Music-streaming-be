package com.findDream.controller;

import com.findDream.domain.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // templates/login.html
    }

    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("user");
        if (loginUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", loginUser);  // ✅ user 객체 전체 추가
        return "home";
    }

}
