package net.pvytykac.resource.projects.representations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * @author Paly
 * @since 2025-02-13
 */
@Value
@Builder
@AllArgsConstructor
public class PatchProjectRepresentation {

    @NotNull
    Op op;
    @NotBlank
    String path;
    Object value;

    public enum Op {
        REMOVE, REPLACE;
    }
}
