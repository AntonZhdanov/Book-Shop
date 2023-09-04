package com.example.bookstore;

import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookStoreApplication {
    private final BookService bookService;

    @Autowired
    public BookStoreApplication(BookService bookService) {
        this.bookService = bookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BookStoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("Introduction to Algorithms, 3rd Edition");
            book.setAuthor("Thomas H. Cormen");
            book.setIsbn("978-0262033848");
            book.setPrice(BigDecimal.valueOf(22));
            book.setDescription("Some books on algorithms are rigorous "
                    + "but incomplete; others cover masses of material "
                    + "but lack rigor.");
            book.setCoverImage("Hardcover");

            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }
}
