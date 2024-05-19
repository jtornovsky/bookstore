package com.interview.bookstore.web.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.interview.bookstore.service.thirdparty.BookProviderService;
import java.util.Arrays;
import java.util.HashSet;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ExternalProviderBookResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookProviderService bookProviderService;

    @Test
    public void testGetBooksByAuthor() throws Exception {
        String author = "Stephen King";
        String title1 = "The Shining";
        String title2 = "It";

        Set<String> bookTitles = new HashSet<>(Arrays.asList(title1, title2));
        when(bookProviderService.getBooksByAuthor(author)).thenReturn(bookTitles);

        // Perform GET request
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get("/api/ext/books/{author}", author)
                    .with(user("user").password("user").roles("USER"))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            // Verify status code and content
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            // Verify response data
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", is(title1)))
            .andExpect(jsonPath("$[1]", is(title2)));

        // Verify that the service method was called with the correct author name
        verify(bookProviderService, times(1)).getBooksByAuthor(author);
    }

    @Test
    public void testGetBooksByAuthorWithEmptyAuthor() throws Exception {
        // Perform GET request with empty author name
        mockMvc
            .perform(
                MockMvcRequestBuilders
                    .get("/api/ext/books/{author}", "AAA")
                    .with(user("user").password("user").roles("USER"))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            // Verify that it returns just OK status
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(0)));
    }
}
