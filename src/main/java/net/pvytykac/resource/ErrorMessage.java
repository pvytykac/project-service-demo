package net.pvytykac.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * @author Paly
 * @since 2025-02-09
 */
@Value
@Builder
@AllArgsConstructor
public class ErrorMessage {

    String error;

}
