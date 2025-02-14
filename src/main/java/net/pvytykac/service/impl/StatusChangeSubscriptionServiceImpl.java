package net.pvytykac.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.pvytykac.db.Project;
import net.pvytykac.db.StatusChangeSubscription;
import net.pvytykac.db.repo.ProjectRepository;
import net.pvytykac.db.repo.StatusChangeSubscriptionRepository;
import net.pvytykac.service.StatusChangeSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Paly
 * @since 2025-02-10
 */
@Slf4j
@Service
@Transactional
public class StatusChangeSubscriptionServiceImpl implements StatusChangeSubscriptionService {

    private final StatusChangeSubscriptionRepository statusChangeSubscriptionRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public StatusChangeSubscriptionServiceImpl(StatusChangeSubscriptionRepository statusChangeSubscriptionRepository,
                                               ProjectRepository projectRepository) {
        this.statusChangeSubscriptionRepository = statusChangeSubscriptionRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public Set<String> getSubscribedProjectIds() {
        log.debug("listing all subscribed project ids");

        return statusChangeSubscriptionRepository.findAllSubscribedProjectIds();
    }

    @Override
    public Set<String> updateSubscriptions(Set<String> unsubscribeFrom, Set<String> subscribeTo) {
        log.info("unsubscribing from projects '{}' and subscribing to projects '{}'", unsubscribeFrom, subscribeTo);

        if (unsubscribeFrom != null && !unsubscribeFrom.isEmpty()) {
            statusChangeSubscriptionRepository.deleteByProjectIds(unsubscribeFrom);
            statusChangeSubscriptionRepository.flush();
        }

        if (subscribeTo != null && !subscribeTo.isEmpty()) {
            var existingSubscriptions = statusChangeSubscriptionRepository.findSubscribedProjectIds(subscribeTo);
            var projectIdsToInsert = subscribeTo.stream()
                    .filter(projectId -> !existingSubscriptions.contains(projectId))
                    .collect(Collectors.toSet());
            var projectsMap = projectRepository.findProjectsByIds(projectIdsToInsert)
                    .stream().collect(Collectors.toMap(Project::getId, Function.identity()));

            statusChangeSubscriptionRepository.saveAllAndFlush(projectIdsToInsert.stream()
                    .map(projectId -> StatusChangeSubscription.builder()
                            .project(projectsMap.get(projectId))
                            .build())
                    .collect(Collectors.toSet()));
        }

        return statusChangeSubscriptionRepository.findAllSubscribedProjectIds();
    }
}
