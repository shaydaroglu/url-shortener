package com.sercan.url_shortener.adapter.in.web;

import com.sercan.url_shortener.application.port.in.ShortUrlUseCase;
import com.sercan.url_shortener.domain.ShortUrl;
import com.sercan.url_shortener.domain.exception.ShortCodeNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UrlShortenerController.class)
@Import(GlobalExceptionHandler.class)
public class UrlShortenerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShortUrlUseCase shortUrlUseCase;

    @Test
    void shouldRedirectToOriginalUrl() throws Exception {
        when(shortUrlUseCase.resolveShortCode("byv5"))
                .thenReturn(URI.create("https://google.com"));

        mockMvc.perform(get("/byv5"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://google.com"));
    }

    @Test
    void shouldReturnNotFoundWhenShortCodeDoesNotExist() throws Exception {
        when(shortUrlUseCase.resolveShortCode("xxxx"))
                .thenThrow(new ShortCodeNotFoundException("xxxx"));

        mockMvc.perform(get("/xxxx"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Short code not found"));
    }

    @Test
    void shouldCreateShortUrl() throws Exception {
        URI originalUrl = URI.create("https://google.com");

        when(shortUrlUseCase.createShortUrl(eq(originalUrl)))
                .thenReturn(new ShortUrl(
                        1L,
                        originalUrl,
                        "byv5",
                        OffsetDateTime.now()
                ));

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "original_url": "https://google.com"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/byv5"))
                .andExpect(jsonPath("$.original_url").value("https://google.com"))
                .andExpect(jsonPath("$.short_code").value("byv5"))
                .andExpect(jsonPath("$.shortened_url").value("http://localhost/byv5"));
    }

    @Test
    void shouldCreateShortUrlWithQueryParameters() throws Exception {
        String originalUrl = "https://www.google.com/search?q=url+shortener&source=test";

        when(shortUrlUseCase.createShortUrl(eq(URI.create(originalUrl))))
                .thenReturn(new ShortUrl(
                        1L,
                        URI.create(originalUrl),
                        "byv5",
                        OffsetDateTime.now()
                ));

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "original_url": "https://www.google.com/search?q=url+shortener&source=test"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/byv5"))
                .andExpect(jsonPath("$.original_url").value(originalUrl))
                .andExpect(jsonPath("$.short_code").value("byv5"))
                .andExpect(jsonPath("$.shortened_url").value("http://localhost/byv5"));
    }

    @Test
    void shouldReturnBadRequestWhenUrlIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "original_url": "https://google"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation failed"));
    }

    @Test
    void shouldReturnBadRequestWhenUrlExceedsAllowedLength() throws Exception {
        String tooLongUrl = "https://example.com/" + "a".repeat(2048);

        String requestBody = """
            {
              "original_url": "%s"
            }
            """.formatted(tooLongUrl);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.detail").value("Original URL must not exceed 2048 characters"));
    }

    @Test
    void shouldReturnBadRequestWhenBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "original_url":
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid request body"));
    }

    @Test
    void shouldReturnBadRequestWhenUrlDoesNotExists() throws Exception {
        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.detail").value("Field 'original_url' is required"));
    }
}
