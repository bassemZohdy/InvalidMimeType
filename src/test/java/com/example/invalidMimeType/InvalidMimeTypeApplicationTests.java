package com.example.invalidMimeType;

import com.example.invalidMimeType.interceptor.InvalidMimeTypeInterceptor;
import com.example.outside.InvalidMimeTypeResponseErrorHandler;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.wiremock.spring.EnableWireMock;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@EnableWireMock
class InvalidMimeTypeApplicationTests {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${wiremock.server.baseUrl}")
    private String wireMockUrl;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    WebClient webClient;


    @Test
    public void testWithWireMock() throws Exception {

        stubFor(get("/test").willReturn(
                aResponse()
                        .withHeader("Content-Type", "csv")
                                .withBody("Mocked Response".getBytes())
                ));


        // Use HttpClient to make a request to the WireMock server
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(wireMockUrl+ "/test"))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("Mocked Response", response.body());

        var restTemplate = restTemplateBuilder
                .interceptors(new InvalidMimeTypeInterceptor())
                //.errorHandler(new InvalidMimeTypeResponseErrorHandler())
                .build();
        var responseEntity =restTemplate.getForEntity(URI.create(wireMockUrl+"/test"),String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(responseEntity.getBody()).isEqualTo("Mocked Response");

//        webClient = WebClient.builder()
//                .baseUrl(wireMockUrl)
//                .build();
//        String responseWebClient = webClient.get()
//                .uri("/test")
//                .retrieve()
//                .bodyToMono(String.class)
//                .block(); // Blocking for simplicity in test
//
//        // Assertions
//        assertThat(responseWebClient).isEqualTo("Mocked Response");

    }

}
