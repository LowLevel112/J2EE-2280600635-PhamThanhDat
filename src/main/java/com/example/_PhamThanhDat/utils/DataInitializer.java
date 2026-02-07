package com.example._PhamThanhDat.utils;

import com.example._PhamThanhDat.entities.*;
import com.example._PhamThanhDat.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    private final ICategoryRepository categoryRepository;
    private final IBookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing database...");

        // 1. Create roles
        if (roleRepository.count() == 0) {
            log.info("Creating roles...");
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
            log.info("Roles created successfully");
        }

        // 2. Create admin user
        if (userRepository.findByUsername("admin").isEmpty()) {
            log.info("Creating admin user...");
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@bookhaven.com");
            admin.setPhone("1234567890");

            // Lấy role ADMIN (lần tìm kiếm đầu tiên hoặc findAll)
            var roles = roleRepository.findAll();
            if (!roles.isEmpty()) {
                // Tìm role có name là ROLE_ADMIN
                var adminRoleOpt = roles.stream()
                        .filter(r -> r.getName().equals("ROLE_ADMIN"))
                        .findFirst();
                if (adminRoleOpt.isPresent()) {
                    admin.setRoles(new HashSet<>(Arrays.asList(adminRoleOpt.get())));
                }
            }

            userRepository.save(admin);
            log.info("Admin user created successfully");
        }

        // 3. Create categories
        if (categoryRepository.count() == 0) {
            log.info("Creating categories...");
            String[] categoryNames = {
                    "Fiction", "Non-Fiction", "Science Fiction", "Mystery",
                    "Romance", "Biography", "History", "Technology",
                    "Self-Help", "Children"
            };

            for (String name : categoryNames) {
                Category category = new Category();
                category.setName(name);
                categoryRepository.save(category);
            }
            log.info("Categories created successfully");
        }

        // 4. Create sample books (or update if they exist)
        log.info("Ensuring sample books with correct data...");
        
        String[][] bookData = {
                { "The Great Gatsby", "F. Scott Fitzgerald", "15.99", "Fiction",
                        "https://images-na.ssl-images-amazon.com/images/P/0743273567.01.L.jpg" },
                { "To Kill a Mockingbird", "Harper Lee", "12.99", "Fiction",
                        "https://images-na.ssl-images-amazon.com/images/P/0061120081.01.L.jpg" },
                { "1984", "George Orwell", "14.99", "Science Fiction",
                        "https://images-na.ssl-images-amazon.com/images/P/0451524934.01.L.jpg" },
                { "Pride and Prejudice", "Jane Austen", "11.99", "Romance",
                        "https://images-na.ssl-images-amazon.com/images/P/0141439513.01.L.jpg" },
                { "The Catcher in the Rye", "J.D. Salinger", "13.99", "Fiction",
                        "https://images-na.ssl-images-amazon.com/images/P/0316769174.01.L.jpg" },
                { "Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "16.99", "Children",
                        "https://images-na.ssl-images-amazon.com/images/P/0439708184.01.L.jpg" },
                { "The Lord of the Rings", "J.R.R. Tolkien", "24.99", "Science Fiction",
                        "https://images-na.ssl-images-amazon.com/images/P/0544003411.01.L.jpg" },
                { "Dune", "Frank Herbert", "18.99", "Science Fiction",
                        "https://images-na.ssl-images-amazon.com/images/P/0441013597.01.L.jpg" },
                { "The Hobbit", "J.R.R. Tolkien", "14.99", "Science Fiction",
                        "https://images-na.ssl-images-amazon.com/images/P/0547928227.01.L.jpg" },
                { "Sapiens: A Brief History of Humankind", "Yuval Noah Harari", "19.99", "Non-Fiction",
                        "https://images-na.ssl-images-amazon.com/images/P/0062316095.01.L.jpg" },
                { "Educated", "Tara Westover", "17.99", "Biography",
                        "https://images-na.ssl-images-amazon.com/images/P/0399590013.01.L.jpg" },
                { "Atomic Habits", "James Clear", "16.99", "Self-Help",
                        "https://images-na.ssl-images-amazon.com/images/P/0735211299.01.L.jpg" },
                { "The Subtle Art of Not Giving a F*ck", "Mark Manson", "15.99", "Self-Help",
                        "https://images-na.ssl-images-amazon.com/images/P/0062457713.01.L.jpg" },
                { "Clean Code", "Robert C. Martin", "35.99", "Technology",
                        "https://images-na.ssl-images-amazon.com/images/P/0132350882.01.L.jpg" },
                { "JavaScript: The Good Parts", "Douglas Crockford", "22.99", "Technology",
                        "https://images-na.ssl-images-amazon.com/images/P/0596517742.01.L.jpg" },
                { "Spring Boot in Action", "Craig Walls", "29.99", "Technology",
                        "https://images-na.ssl-images-amazon.com/images/P/1617292540.01.L.jpg" },
                { "MySQL Cookbook", "Paul DuBois", "32.99", "Technology",
                        "https://images-na.ssl-images-amazon.com/images/P/0596001452.01.L.jpg" },
                { "The Pragmatic Programmer", "Andrew Hunt", "28.99", "Technology",
                        "https://images-na.ssl-images-amazon.com/images/P/0135957052.01.L.jpg" },
                { "Design Patterns", "Gang of Four", "34.99", "Technology",
                        "https://images-na.ssl-images-amazon.com/images/P/0201633612.01.L.jpg" },
                { "Refactoring", "Martin Fowler", "31.99", "Technology",
                        "https://images-na.ssl-images-amazon.com/images/P/0201485672.01.L.jpg" }
        };

        for (String[] data : bookData) {
            var existingBook = bookRepository.findAll().stream()
                    .filter(b -> b.getTitle().equalsIgnoreCase(data[0]))
                    .findFirst();

            if (existingBook.isPresent()) {
                // Update existing book with correct image URL if missing
                Book book = existingBook.get();
                if (book.getImageUrl() == null || book.getImageUrl().isEmpty()) {
                    log.info("Updating book '{}' with image URL", data[0]);
                    book.setImageUrl(data[4]);
                    bookRepository.save(book);
                }
            } else {
                // Create new book if doesn't exist
                Category category = categoryRepository.findByName(data[3])
                        .orElse(null);

                if (category != null) {
                    Book book = new Book();
                    book.setTitle(data[0]);
                    book.setAuthor(data[1]);
                    book.setPrice(Double.parseDouble(data[2]));
                    book.setImageUrl(data[4]);
                    book.setCategory(category);
                    bookRepository.save(book);
                }
            }
        }
        log.info("Sample books ensured successfully");

        log.info("Database initialization completed!");
    }
}
