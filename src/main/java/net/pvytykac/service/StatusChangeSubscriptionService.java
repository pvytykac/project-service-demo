package net.pvytykac.service;

import java.util.Set;

/**
 * @author Paly
 * @since 2025-02-10
 */
public interface StatusChangeSubscriptionService {

    Set<String> getSubscribedProjectIds();
    Set<String> updateSubscriptions(Set<String> unsubscribeFrom, Set<String> subscribeTo);

}
