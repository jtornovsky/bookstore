package com.interview.bookstore.service.thirdparty.nytimes;

import com.interview.bookstore.config.ApplicationProperties;
import com.interview.bookstore.service.thirdparty.BookProviderService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NewYorkTimesBookProviderService implements BookProviderService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    public NewYorkTimesBookProviderService(ApplicationProperties applicationProperties) {
        this.restTemplate = new RestTemplate();
        this.apiKey = applicationProperties.getApi().getNytimes().getKey();
        this.baseUrl = applicationProperties.getApi().getNytimes().getBaseurl();
    }

    @Override
    public Set<String> getBooksByAuthor(String author) {
        String url = baseUrl + "/reviews.json?author=" + author + "&api-key=" + apiKey;
        ReviewResponse reviewResponse = restTemplate.getForObject(url, ReviewResponse.class);
        if (reviewResponse != null && reviewResponse.getNum_results() > 0) {
            return extractBooks(reviewResponse.getResults());
        }
        return null;
    }

    private Set<String> extractBooks(List<ReviewResult> results) {
        Set<String> books = new HashSet<>();
        for (ReviewResult result : results) {
            String title = result.getBook_title();
            books.add(title);
        }
        return books;
    }
}
