package net.pvytykac.resource.projects.representations;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
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
    @UUID
    String groupId;
    @NotNull
    @Valid
    StatusRepresentation status;

}
