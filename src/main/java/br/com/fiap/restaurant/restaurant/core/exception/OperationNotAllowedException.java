package br.com.fiap.restaurant.restaurant.core.exception;

public class OperationNotAllowedException extends BusinessException {

    public OperationNotAllowedException(String message) {
        super(message);
    }

    public OperationNotAllowedException() {
        this("The current user does not have permission to perform this action.");
    }
}
