package net.pvytykac.resource;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.pvytykac.service.EntityDoesNotExistException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.Objects;

/**
 * @author Paly
 * @since 2025-02-09
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlers {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public @ResponseBody ErrorResponse handleConflict(DataIntegrityViolationException ex) {
        var message = "unique constraint violation: '" + ex.getMessage() + "'";

        return new ErrorResponse(message);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityDoesNotExistException.class)
    public @ResponseBody ErrorResponse handleEntityDoesNotExist(EntityDoesNotExistException ex) {
        var message = "entity '" + ex.getEntityName() + "' with id '" + ex.getEntityId() + "' does not exist";

        return new ErrorResponse(message);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ListErrorResponse<Error> validationError(HandlerMethodValidationException exception) {
        var errors = exception.getAllErrors().stream()
                .map(err -> {
                    var fieldName = ((DefaultMessageSourceResolvable) Objects.requireNonNull(err.getArguments())[0]).getDefaultMessage();
                    var errorMessage = err.getDefaultMessage();
                    return new Error(fieldName, errorMessage);
                }).toList();

        return new ListErrorResponse<>(errors);
    }

    @Value
    public static class ErrorResponse {
        String error;
    }

    @Value
    public static class ListErrorResponse<T> {
        List<T> errors;
    }

    @Value
    public static class Error {
        String field;
        String message;
    }
}
