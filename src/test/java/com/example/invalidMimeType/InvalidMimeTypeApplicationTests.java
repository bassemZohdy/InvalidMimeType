package com.example.invalidMimeType;

import com.example.invalidMimeType.interceptor.InvalidMimeTypeInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InvalidMimeTypeApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    public void setUp() {
        // Add the InvalidMimeTypeInterceptor to the RestTemplate before each test
        testRestTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList(new InvalidMimeTypeInterceptor())
        );
    }

    @Test
    void testInvalidMimeType() {
        // Make a GET request to the controller endpoint
        ResponseEntity<String> response = restTemplate.getForEntity("/test", String.class);

        // Assert response status
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

        // Assert response body
        assertThat(response.getBody()).isEqualTo("test");

        // Assert custom header
        assertThat(response.getHeaders().getFirst("Content-Type")).isNull();
    }

}
