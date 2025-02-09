package net.pvytykac.resource.groups.representations;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.hibernate.validator.constraints.UUID;

/**
 * @author Paly
 * @since 2025-02-08
 */
@Value
@AllArgsConstructor
@Builder
public class GroupRepresentation {

    @UUID
    String id;
    @NotBlank
    String name;

}
