package com.example._PhamThanhDat.viewmodels;

import com.example._PhamThanhDat.entities.Book;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BookGetVm(Long id, String title, String author, Double price, String category, String imageUrl) {
    public static BookGetVm from(@NotNull Book book) {
        return BookGetVm.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .category(book.getCategory().getName()) // Chỉ lấy tên Category cho gọn
                .imageUrl(book.getImageUrl())
                .build();
    }
}