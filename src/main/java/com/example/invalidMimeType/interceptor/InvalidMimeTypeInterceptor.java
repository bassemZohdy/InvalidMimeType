package com.example.invalidMimeType.interceptor;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeType;

import java.io.IOException;

public class InvalidMimeTypeInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        var contentType = response.getHeaders().getFirst("Content-Type");
        if (contentType != null) {
            try {

                MimeType.valueOf(contentType);
            } catch (InvalidMimeTypeException e) {
                response.getHeaders().set("Content-Type", "application/octet-stream");
            }
        }
        return response;
    }
}
