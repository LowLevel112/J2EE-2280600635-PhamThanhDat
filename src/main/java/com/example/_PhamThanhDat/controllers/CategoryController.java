package com.example._PhamThanhDat.controllers;

import java.util.List;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example._PhamThanhDat.entities.Category;
import com.example._PhamThanhDat.services.CategoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // --- 1. DANH SÁCH (LIST) ---
    @GetMapping
    public String showAllCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "category/list";
    }

    // --- 2. THÊM MỚI (ADD) ---
    @GetMapping("/add")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/add";
    }

    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("category") Category category,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "category/add";
        }
        categoryService.addCategory(category);
        return "redirect:/categories";
    }

    // --- 3. CHỈNH SỬA (EDIT) ---
    @GetMapping("/edit/{id}")
    public String editCategoryForm(@NotNull Model model, @PathVariable long id) {
        var category = categoryService.getCategoryById(id);
        model.addAttribute("category", category.orElseThrow(() -> new IllegalArgumentException("Category not found")));
        return "category/edit";
    }

    @PostMapping("/edit")
    public String editCategory(@Valid @ModelAttribute("category") Category category,
            @NotNull BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "category/edit";
        }
        categoryService.updateCategory(category);
        return "redirect:/categories";
    }

    // --- 4. XÓA (DELETE) ---
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable long id) {
        categoryService.deleteCategoryById(id);
        return "redirect:/categories";
    }
}
