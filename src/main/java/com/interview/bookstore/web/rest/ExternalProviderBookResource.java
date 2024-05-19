package com.interview.bookstore.web.rest;

import com.interview.bookstore.service.thirdparty.BookProviderService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ext")
public class ExternalProviderBookResource {

    @Qualifier("newYorkTimesBookProviderService")
    private BookProviderService bookProviderService;

    @Autowired
    public ExternalProviderBookResource(BookProviderService bookProviderService) {
        this.bookProviderService = bookProviderService;
    }

    @GetMapping("/books/{author}")
    public Set<String> getBooksByAuthor(@PathVariable String author) {
        return bookProviderService.getBooksByAuthor(author);
    }
}
