package com.interview.bookstore.service.thirdparty;

import com.interview.bookstore.service.ExternalApiException;
import java.util.Optional;
import java.util.Set;

public interface BookProviderService {
    Optional<Set<String>> getBooksByAuthor(String author) throws ExternalApiException;
}
