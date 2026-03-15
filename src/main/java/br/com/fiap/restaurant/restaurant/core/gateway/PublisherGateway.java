package br.com.fiap.restaurant.restaurant.core.gateway;

import java.util.concurrent.CompletableFuture;

public interface PublisherGateway<E> {
    CompletableFuture<Void> publish(E event);
}
