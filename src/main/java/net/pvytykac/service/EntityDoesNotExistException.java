package net.pvytykac.service;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Paly
 * @since 2025-02-09
 */
@Value
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EntityDoesNotExistException extends RuntimeException {

    String entityName;
    String entityId;

}
