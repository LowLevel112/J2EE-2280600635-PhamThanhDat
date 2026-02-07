package com.example._PhamThanhDat.services;

import com.example._PhamThanhDat.entities.Book;
import com.example._PhamThanhDat.repositories.IBookRepository;
import com.example._PhamThanhDat.repositories.ICategoryRepository;
import com.example._PhamThanhDat.viewmodels.BookPostVm;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = { Exception.class, Throwable.class })
public class BookService {

    private final IBookRepository bookRepository;
    private ICategoryRepository categoryRepository;

    public List<Book> getAllBooks(Integer pageNo,
            Integer pageSize,
            String sortBy) {
        return bookRepository.findAllBooks(pageNo, pageSize, sortBy);
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public void updateBook(Book book) {
        Book existingBook = bookRepository.findById(book.getId())
                .orElse(null);
        Objects.requireNonNull(existingBook).setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPrice(book.getPrice());
        existingBook.setCategory(book.getCategory());
        bookRepository.save(existingBook);
    }

    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBook(String keyword) {
        return bookRepository.searchBook(keyword);
    }

    public void addBookFromVm(BookPostVm bookPostVm) {
        var book = new Book();
        book.setTitle(bookPostVm.title());
        book.setAuthor(bookPostVm.author());
        book.setPrice(bookPostVm.price());

        categoryRepository.findById(bookPostVm.categoryId())
                .ifPresent(book::setCategory);

        bookRepository.save(book);
    }

    public void updateBookFromVm(Long id, BookPostVm bookPostVm) {
        bookRepository.findById(id).ifPresent(book -> {
            book.setTitle(bookPostVm.title());
            book.setAuthor(bookPostVm.author());
            book.setPrice(bookPostVm.price());

            categoryRepository.findById(bookPostVm.categoryId())
                    .ifPresent(book::setCategory);

            bookRepository.save(book);
        });
    }

    public List<Book> getFeaturedBooks() {
        return bookRepository.findAllBooks(0, 20, "id");
    }

}