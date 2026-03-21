package br.com.fiap.restaurant.restaurant.infra.config;

import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.exception.InvalidCredentialsException;
import br.com.fiap.restaurant.restaurant.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.restaurant.infra.controller.response.FieldErrorResponse;
import br.com.fiap.restaurant.restaurant.infra.controller.response.SimpleErrorResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ErrorHandle {

    private final MessageSource messageSource;

    public ErrorHandle(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ApiResponse(responseCode = "403", description = "Operação não permitida",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
    @ResponseStatus(code =  HttpStatus.FORBIDDEN)
    @ExceptionHandler({OperationNotAllowedException.class})
    public SimpleErrorResponse handleOperationNotAllowedException(OperationNotAllowedException e) {
        return new SimpleErrorResponse(e.getMessage());
    }

    @ApiResponse(responseCode = "403", description = "Acesso negado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public SimpleErrorResponse handleAccessDeniedException(AccessDeniedException e) {
        return new SimpleErrorResponse(e.getMessage());
    }

    @ApiResponse(responseCode = "401", description = "Não autorizado - Usuário não encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UsernameNotFoundException.class)
    public SimpleErrorResponse handleUsernameNotFoundException(UsernameNotFoundException e) {
        return new SimpleErrorResponse(e.getMessage());
    }

    @ApiResponse(responseCode = "401", description = "Não autorizado - Credenciais inválidas",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialsException.class)
    public SimpleErrorResponse handleInvalidCredentialsException(InvalidCredentialsException e) {
        return new SimpleErrorResponse(e.getMessage());
    }

    @ApiResponse(responseCode = "400", description = "Requisição inválida - Erro de negócio",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public SimpleErrorResponse handleBusinessException(BusinessException e) {
        return new SimpleErrorResponse(e.getMessage());
    }

    @ApiResponse(responseCode = "400", description = "Requisição inválida - Erro de validação de dados",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FieldErrorResponse.class))))
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<FieldErrorResponse> handle(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        List<FieldErrorResponse> errors = new ArrayList<>();

        fieldErrors.forEach(error -> {
            String field  = error.getField();
            String message = messageSource.getMessage(error, LocaleContextHolder.getLocale());
            errors.add(new FieldErrorResponse(field, message));
        });

        return errors;
    }
}
