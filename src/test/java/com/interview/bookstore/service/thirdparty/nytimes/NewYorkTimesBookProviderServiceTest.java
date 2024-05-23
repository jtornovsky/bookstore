package com.interview.bookstore.service.thirdparty.nytimes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.interview.bookstore.config.ApplicationProperties;
import com.interview.bookstore.service.ExternalApiException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class NewYorkTimesBookProviderServiceTest {

    private NewYorkTimesBookProviderService bookProviderService;

    private final String AUTHOR = "Stephen King";

    private final Logger logger = LoggerFactory.getLogger(NewYorkTimesBookProviderService.class.getName());
    private final ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
    private final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    private final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    @BeforeEach
    void setUp() {
        bookProviderService = new NewYorkTimesBookProviderService(new ApplicationProperties());
        logbackLogger.setLevel(ch.qos.logback.classic.Level.toLevel(Level.DEBUG.toString()));

        loggerContext.getLogger(NewYorkTimesBookProviderService.class).addAppender(listAppender);
        listAppender.start();
    }

    @AfterEach
    void cleanUp() {
        listAppender.stop();
    }

    @Test
    void testGetBooksByAuthor_success() throws ExternalApiException {
        final String title1 = "Title 1";
        final String title2 = "Title 2";

        ReviewResponse mockResponse = new ReviewResponse();
        ReviewResult result1 = new ReviewResult();
        result1.setBook_title(title1);
        ReviewResult result2 = new ReviewResult();
        result2.setBook_title(title2);
        ReviewResult result3 = new ReviewResult();
        result3.setBook_title(null);
        mockResponse.setResults(List.of(result1, result2, result3));
        mockResponse.setNum_results(3);

        // Spy the service to mock the fetchReviewsFromApi method
        NewYorkTimesBookProviderService spyService = Mockito.spy(bookProviderService);
        Mockito.doReturn(mockResponse).when(spyService).fetchReviewsFromApi(anyString());

        Set<String> expectedBooks = new HashSet<>();
        expectedBooks.add(title1);
        expectedBooks.add(title2);

        Set<String> actualBooks = spyService.getBooksByAuthor(AUTHOR).get();

        assertEquals(expectedBooks, actualBooks);

        // Verify log messages
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(4, logsList.size());
        logsList
            .stream()
            .filter(logMsg ->
                logMsg.getFormattedMessage().equals("Found " + mockResponse.getNum_results() + " reviews of the author " + AUTHOR) &&
                Level.DEBUG.equals(logMsg.getLevel())
            )
            .findAny()
            .get();
        logsList
            .stream()
            .filter(logMsg -> logMsg.getFormattedMessage().equals("Found title: " + title1) && Level.DEBUG.equals(logMsg.getLevel()))
            .findAny()
            .get();
        logsList
            .stream()
            .filter(logMsg -> logMsg.getFormattedMessage().equals("Found title: " + title2) && Level.DEBUG.equals(logMsg.getLevel()))
            .findAny()
            .get();
        logsList
            .stream()
            .filter(logMsg ->
                logMsg
                    .getFormattedMessage()
                    .equals(
                        "Failed to extract book title from the review result: ReviewResult{book_title='null', book_author='null', summary='null'}"
                    ) &&
                Level.ERROR.equals(logMsg.getLevel())
            )
            .findAny()
            .get();
    }

    @Test
    void testGetBooksByEmptyAuthor_success() throws ExternalApiException {
        ReviewResponse mockResponse = new ReviewResponse();

        // Spy the service to mock the fetchReviewsFromApi method
        NewYorkTimesBookProviderService spyService = Mockito.spy(bookProviderService);
        Mockito.doReturn(mockResponse).when(spyService).fetchReviewsFromApi(anyString());

        Set<String> actualBooks = spyService.getBooksByAuthor(AUTHOR).get();
        assertTrue(actualBooks.isEmpty());

        // Verify log messages
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        logsList
            .stream()
            .filter(logMsg ->
                logMsg.getFormattedMessage().equals("No reviews found for the author: " + AUTHOR) && Level.INFO.equals(logMsg.getLevel())
            )
            .findAny()
            .get();
    }

    @Test
    void getBooksByAuthor_apiError() {
        final String runtimeException = "Unexpected error";
        final String expectedErrorMessage = "Error fetching reviews from API: " + runtimeException;

        // Spy the service to mock the fetchReviewsFromApi method
        NewYorkTimesBookProviderService spyService = spy(bookProviderService);
        doThrow(new RuntimeException(runtimeException)).when(spyService).fetchReviewsFromApi(AUTHOR);

        // Call the method and assert the exception
        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> spyService.getBooksByAuthor(AUTHOR));

        assertEquals(expectedErrorMessage, exception.getMessage());

        // Verify log messages
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        logsList
            .stream()
            .filter(logMsg ->
                logMsg.getFormattedMessage().equals("Error fetching reviews from API for author " + AUTHOR + ": " + runtimeException) &&
                Level.ERROR.equals(logMsg.getLevel())
            )
            .findAny()
            .get();
    }
}
