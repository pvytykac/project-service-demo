package net.pvytykac.resource;

import net.pvytykac.service.EntityDoesNotExistException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.stream.Collectors;

/**
 * @author Paly
 * @since 2025-02-09
 */
@ControllerAdvice
public class GlobalExceptionHandlers {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public @ResponseBody ErrorMessage handleConflict(DataIntegrityViolationException ex) {
        return ErrorMessage.builder()
                .error("unique constraint violation: '" + ex.getMessage() + "'")
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityDoesNotExistException.class)
    public @ResponseBody ErrorMessage handleEntityDoesNotExist(EntityDoesNotExistException ex) {
        return ErrorMessage.builder()
                .error("entity '" + ex.getEntityName() + "' with id '" + ex.getEntityId() + "' does not exist")
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public @ResponseBody ErrorMessage handleValidationException(HandlerMethodValidationException ex) {
        //todo: good enough for now, change to list of field:error msg pairs
        var msg = ex.getBeanResults().stream()
                .filter(ParameterErrors::hasErrors)
                .map(res -> res.getNestedPath() + ": " + res.getAllErrors().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.joining(", "));

        return ErrorMessage.builder()
                .error(msg)
                .build();
    }

}
