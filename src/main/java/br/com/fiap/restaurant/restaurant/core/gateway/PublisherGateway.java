package br.com.fiap.restaurant.restaurant.core.gateway;

import java.util.concurrent.Future;

public interface PublisherGateway<T> {
    Future<Void> publish(T t);
}
