package com.interview.bookstore.web.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.interview.bookstore.service.ExternalApiException;
import com.interview.bookstore.service.thirdparty.BookProviderService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ExternalProviderBookResourceTest {

    private final String URI = "/api/ext/books/{author}";
    private final String AUTHOR = "Stephen King";
    private final String UNKNOWN_AUTHOR = "Unknown Author";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookProviderService bookProviderService;

    @Test
    public void testGetBooksByAuthor() throws Exception {
        String title1 = "The Shining";
        String title2 = "It";

        Optional<Set<String>> bookTitles = Optional.of(new HashSet<>(Arrays.asList(title1, title2)));
        when(bookProviderService.getBooksByAuthor(AUTHOR)).thenReturn(bookTitles);

        // Perform GET request
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders
                .get(URI, AUTHOR)
                .with(user("user").password("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
        );
        // Verify result
        response
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            // Verify response data
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", is(title1)))
            .andExpect(jsonPath("$[1]", is(title2)));

        // Verify that the service method was called with the correct author name
        verify(bookProviderService, times(1)).getBooksByAuthor(AUTHOR);
    }

    @Test
    public void testGetBooksByAuthorWithEmptyAuthor() throws Exception {
        when(bookProviderService.getBooksByAuthor(UNKNOWN_AUTHOR)).thenReturn(Optional.of(new HashSet<>()));

        // Perform GET request with wrong author name
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders
                .get(URI, UNKNOWN_AUTHOR)
                .with(user("user").password("user").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
        );
        // Verify result
        response
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getBooksByAuthor_externalApiException() throws ExternalApiException {
        doThrow(new ExternalApiException("API error")).when(bookProviderService).getBooksByAuthor(UNKNOWN_AUTHOR);

        ExternalApiException exception = assertThrows(
            ExternalApiException.class,
            () -> bookProviderService.getBooksByAuthor(UNKNOWN_AUTHOR)
        );

        assertEquals("API error", exception.getMessage());
    }
}
