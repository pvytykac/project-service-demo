package net.pvytykac.resource.subscriptions.representations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

/**
 * @author Paly
 * @since 2025-02-10
 */
@Value
@Builder
@AllArgsConstructor
public class PatchStatusChangeSubscriptionsRepresentation {

    Set<String> subscribeTo;
    Set<String> unsubscribeFrom;

}
