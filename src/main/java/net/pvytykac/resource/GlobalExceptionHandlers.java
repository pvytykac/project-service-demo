package net.pvytykac.resource;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Paly
 * @since 2025-02-09
 */
@ControllerAdvice
public class GlobalExceptionHandlers {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public @ResponseBody ErrorMessage handleConflict() {
        return ErrorMessage.builder()
                .error("unique constraint violation")
                .build();
    }

}
