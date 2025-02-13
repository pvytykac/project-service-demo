package net.pvytykac.resource.projects.representations;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import net.pvytykac.db.Status;

/**
 * @author Paly
 * @since 2025-02-13
 */
@Value
@Builder
@AllArgsConstructor
public class StatusRepresentation {

    @NotNull
    Status reportedStatus;
    Status overriddenStatus;

}
