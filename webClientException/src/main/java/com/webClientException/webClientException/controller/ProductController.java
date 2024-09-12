package com.webClientException.webClientException.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Supplier;

@RestController
public class ProductController {
    private Logger log = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/hello")
    public String hello(){
        return "hello HJ";
    }

    @GetMapping("/product")
    public Mono<FinalResponse> getProduct() {
        System.out.println("product");
        return WebClient.builder()
                .baseUrl("https://fakestoreapi.com/products/1")
                .build()
                .get()
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Internal server error: {}", response.statusCode());
                    return Mono.error(new Exception("Internal server error"));
                })
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error("Client error: {}", response.statusCode());
                    return Mono.error((Supplier<? extends Throwable>) new Exception("Client error"));
                })
                //     .onRawStatus()
                .bodyToMono(FinalResponse.class);
    }

//    @GetMapping("/products")
//    public Mono<List<FinalResponse>> getAllProduct() {
//        return WebClient.builder()
//                .baseUrl("https://fakestoreapi.com/products")
//                .build()
//                .get()
//                .retrieve()
//                .onStatus(HttpStatusCode::is5xxServerError, response -> {
//                    log.error("Internal server error: {}", response.statusCode());
//                    return Mono.error(new Exception("Internal server error"));
//                })
//                .onStatus(HttpStatusCode::is4xxClientError, response -> {
//                    log.error("Client error: {}", response.statusCode());
//                    return Mono.error((Supplier<? extends Throwable>) new Exception("Client error"));
//                })
//                .bodyToMono(new ParameterizedTypeReference<List<FinalResponse>>() {
//                })
//                .flatMapMany(Flux::fromIterable) // Convert the Mono<List<FinalResponse>> to a Flux<FinalResponse>
//                .collectList() // Collect all elements of the Flux into a List
//                .doOnError(WebClientResponseException.class, ex -> {
//                    log.error("WebClientResponseException occurred: {}", ex.getMessage());
//                })
//                .doOnError(Throwable.class, ex -> {
//                    log.error("An unexpected error occurred: {}", ex.getMessage());
//                });
//    }
@GetMapping("/products")
public Mono<List<FinalResponse>> getAllProducts() {
    return WebClient.builder()
            .baseUrl("https://fakestoreapi.com") // Updated baseUrl
            .build()
            .get()
            .uri(uriBuilder -> uriBuilder.path("/products").queryParam("invalid", "value").build()) // Add invalid parameter
            .retrieve()
            .onStatus(HttpStatusCode::is5xxServerError, response -> {
                log.error("Internal server error: {}", response.statusCode());
                return Mono.error(new Exception("Internal server error"));
            })
            .onStatus(HttpStatusCode::is4xxClientError, response -> {
                log.error("Client error: {}", response.statusCode());
                return Mono.error(new Exception("Client error"));
            })
            .bodyToMono(new ParameterizedTypeReference<List<FinalResponse>>() {})
            .flatMapMany(Flux::fromIterable) // Convert the Mono<List<FinalResponse>> to a Flux<FinalResponse>
            .collectList() // Collect all elements of the Flux into a List
            .doOnError(WebClientResponseException.class, ex -> {
                log.error("WebClientResponseException occurred: {}", ex.getMessage());
            })
            .doOnError(Throwable.class, ex -> {
                log.error("An unexpected error occurred: {}", ex.getMessage());
            });
}



}
