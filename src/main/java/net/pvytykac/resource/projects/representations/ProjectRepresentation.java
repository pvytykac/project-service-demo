package net.pvytykac.resource.projects.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import net.pvytykac.db.Status;
import net.pvytykac.resource.groups.representations.GroupRepresentation;
import org.hibernate.validator.constraints.UUID;

/**
 * @author Paly
 * @since 2025-02-09
 */
@Value
@Builder
@AllArgsConstructor
public class ProjectRepresentation {

    @UUID
    String id;
    @NotBlank
    String name;
    @NotNull
    @Valid
    GroupRepresentation group;
    @NotNull
    Status reportedStatus;
    Status statusOverride;

    @JsonProperty
    public Status getEffectiveStatus() {
        return statusOverride == null
                ? reportedStatus
                : statusOverride;
    }

}
