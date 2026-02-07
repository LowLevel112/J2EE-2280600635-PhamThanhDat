package com.example._PhamThanhDat.controllers;

import com.example._PhamThanhDat.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("featuredBooks", bookService.getFeaturedBooks());
        return "home/index";
    }
}