package net.pvytykac.resource.subscriptions;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import net.pvytykac.resource.GenericCollectionRepresentation;
import net.pvytykac.resource.subscriptions.representations.PatchStatusChangeSubscriptionsRepresentation;
import net.pvytykac.service.StatusChangeSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Paly
 * @since 2025-02-10
 */
@RestController
@RequestMapping("/v1/me/status-change-subscriptions")
public class StatusChangeSubscriptionResource {

    private final StatusChangeSubscriptionService service;

    @Autowired
    public StatusChangeSubscriptionResource(StatusChangeSubscriptionService service) {
        this.service = service;
    }

    @GetMapping
    public GenericCollectionRepresentation<String> getStatusChangeSubscriptions() {
        return new GenericCollectionRepresentation<>(service.getSubscribedProjectIds());
    }

    @PatchMapping
    public GenericCollectionRepresentation<String> patchStatusChangeSubscriptions(
            @RequestBody @Valid @NotNull PatchStatusChangeSubscriptionsRepresentation representation) {
        var subscriptions = service.updateSubscriptions(representation.getUnsubscribeFrom(), representation.getSubscribeTo());

        return new GenericCollectionRepresentation<>(subscriptions);
    }

}
