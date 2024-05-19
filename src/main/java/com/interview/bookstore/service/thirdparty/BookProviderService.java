package com.interview.bookstore.service.thirdparty;

import java.util.Set;

public interface BookProviderService {
    Set<String> getBooksByAuthor(String author);
}
