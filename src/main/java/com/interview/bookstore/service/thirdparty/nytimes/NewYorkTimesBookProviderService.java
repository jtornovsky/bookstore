package com.interview.bookstore.service.thirdparty.nytimes;

import com.interview.bookstore.config.ApplicationProperties;
import com.interview.bookstore.service.ExternalApiException;
import com.interview.bookstore.service.thirdparty.BookProviderService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.shaded.com.google.common.annotations.VisibleForTesting;

@Service
public class NewYorkTimesBookProviderService implements BookProviderService {

    private final Logger log = LoggerFactory.getLogger(NewYorkTimesBookProviderService.class);

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    public NewYorkTimesBookProviderService(ApplicationProperties applicationProperties) {
        this.restTemplate = new RestTemplate();
        this.apiKey = applicationProperties.getApi().getNytimes().getKey();
        this.baseUrl = applicationProperties.getApi().getNytimes().getBaseurl();
    }

    @Override
    public Optional<Set<String>> getBooksByAuthor(String author) throws ExternalApiException {
        try {
            ReviewResponse reviewResponse = fetchReviewsFromApi(author);
            if (reviewResponse != null && reviewResponse.getNum_results() > 0) {
                log.debug("Found {} reviews of the author {}", reviewResponse.getNum_results(), author);
                return Optional.of(extractBooks(reviewResponse.getResults()));
            }
        } catch (RuntimeException e) {
            log.error("Error fetching reviews from API for author {}: {}", author, e.getMessage(), e);
            throw new ExternalApiException("Error fetching reviews from API: " + e.getMessage());
        }
        log.info("No reviews found for the author: {}", author);
        return Optional.of(new HashSet<>());
    }

    @VisibleForTesting
    ReviewResponse fetchReviewsFromApi(String author) {
        String url = baseUrl + "/reviews.json?author=" + author + "&api-key=" + apiKey;
        log.debug("Fetching reviews from {}", url);
        return restTemplate.getForObject(url, ReviewResponse.class);
    }

    private Set<String> extractBooks(List<ReviewResult> results) {
        Set<String> books = new HashSet<>();
        for (ReviewResult result : results) {
            String title = result.getBook_title();
            if (title == null) {
                log.error("Failed to extract book title from the review result: {}", result);
                continue;
            }
            log.debug("Found title: {}", title);
            books.add(title);
        }
        return books;
    }
}
