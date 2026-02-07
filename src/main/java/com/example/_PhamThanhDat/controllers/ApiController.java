package com.example._PhamThanhDat.controllers;

import com.example._PhamThanhDat.services.BookService;
import com.example._PhamThanhDat.services.CategoryService;
import com.example._PhamThanhDat.viewmodels.BookGetVm;
import com.example._PhamThanhDat.viewmodels.BookPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*") // Cho phép gọi từ bất kỳ nguồn nào
@RequiredArgsConstructor
public class ApiController {
    private final BookService bookService;
    private final CategoryService categoryService;

    // --- LẤY THÔNG TIN USER HIỆN TẠI ---
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(new java.util.HashMap<String, Object>() {
                {
                    put("authenticated", false);
                    put("roles", java.util.List.of());
                }
            });
        }

        var authorities = authentication.getAuthorities()
                .stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity.ok(new java.util.HashMap<String, Object>() {
            {
                put("authenticated", true);
                put("username", authentication.getName());
                put("roles", authorities);
            }
        });
    }

    // --- LẤY DANH SÁCH SÁCH ---
    @GetMapping("/books")
    public ResponseEntity<List<BookGetVm>> getAllBooks(Integer pageNo, Integer pageSize, String sortBy) {
        return ResponseEntity.ok(bookService.getAllBooks(
                pageNo == null ? 0 : pageNo,
                pageSize == null ? 20 : pageSize,
                sortBy == null ? "id" : sortBy)
                .stream()
                .map(BookGetVm::from)
                .toList());
    }

    // --- LẤY SÁCH THEO ID ---
    @GetMapping("/books/id/{id}")
    public ResponseEntity<BookGetVm> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id)
                .map(BookGetVm::from)
                .orElse(null));
    }

    // --- XÓA SÁCH ---
    @DeleteMapping("/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.ok().build();
    }

    // --- TÌM KIẾM SÁCH ---
    @GetMapping("/books/search")
    public ResponseEntity<List<BookGetVm>> searchBooks(String keyword) {
        return ResponseEntity.ok(bookService.searchBook(keyword)
                .stream()
                .map(BookGetVm::from)
                .toList());
    }

    // --- THÊM SÁCH MỚI
    @PostMapping("/books")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addBook(@RequestBody BookPostVm bookPostVm) {
        bookService.addBookFromVm(bookPostVm);
        return ResponseEntity.ok().build();
    }

    // --- CẬP NHẬT SÁCH
    @PostMapping("/books/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateBook(@PathVariable Long id, @RequestBody BookPostVm bookPostVm) {
        bookService.updateBookFromVm(id, bookPostVm);
        return ResponseEntity.ok().build();
    }

}