package br.com.fiap.restaurant.restaurant.core.gateway;

import java.util.concurrent.Future;

public interface NotifierGateway <T> {
    Future<Void> send(T t);
}
