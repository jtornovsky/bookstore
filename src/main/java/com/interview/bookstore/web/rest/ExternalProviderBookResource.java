package com.interview.bookstore.web.rest;

import com.interview.bookstore.service.ExternalApiException;
import com.interview.bookstore.service.thirdparty.BookProviderService;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ext")
public class ExternalProviderBookResource {

    @Qualifier("newYorkTimesBookProviderService")
    private final BookProviderService bookProviderService;

    @Autowired
    public ExternalProviderBookResource(BookProviderService bookProviderService) {
        this.bookProviderService = bookProviderService;
    }

    @GetMapping("/books/{author}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Set<String>> getBooksByAuthor(@PathVariable String author) throws ExternalApiException {
        return bookProviderService.getBooksByAuthor(author);
    }
}
