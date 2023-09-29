package com.example.bookstore.repository.book;

import com.example.bookstore.dto.book.BookSearchParametersDto;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.SpecificationBuilder;
import com.example.bookstore.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> spec = Specification.where(null);
        spec = appendSpec(spec, BookField.TITLE, searchParameters.title());
        spec = appendSpec(spec, BookField.AUTHOR, searchParameters.author());
        spec = appendSpec(spec, BookField.ISBN, searchParameters.isbn());
        spec = appendSpec(spec, BookField.PRICE, searchParameters.price());
        spec = appendSpec(spec, BookField.DESCRIPTION, searchParameters.description());
        spec = appendSpec(spec, BookField.COVER_IMAGE, searchParameters.coverImage());
        return spec;
    }

    private Specification<Book> appendSpec(Specification<Book> spec,
                                           BookField field, String[] searchValue) {
        if (searchValue != null && searchValue.length > 0) {
            spec = spec.and(bookSpecificationProviderManager
                    .getSpecificationProvider(field.getKey())
                    .getSpecification(searchValue));
        }
        return spec;
    }
}
