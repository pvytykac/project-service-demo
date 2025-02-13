package net.pvytykac.service.impl;

import net.pvytykac.EmbeddedPostgresConfiguration;
import net.pvytykac.db.Group;
import net.pvytykac.db.Project;
import net.pvytykac.db.Status;
import net.pvytykac.db.StatusChangeSubscription;
import net.pvytykac.db.repo.GroupRepository;
import net.pvytykac.db.repo.ProjectRepository;
import net.pvytykac.db.repo.StatusChangeSubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED;

/**
 * @author Paly
 * @since 2025-02-10
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AUTO_CONFIGURED)
@ContextConfiguration(classes = EmbeddedPostgresConfiguration.class)
@ComponentScan(basePackages = "net.pvytykac")
class StatusChangeSubscriptionServiceImplTest {

    private final StatusChangeSubscriptionServiceImpl service;
    private final StatusChangeSubscriptionRepository statusChangeSubscriptionRepository;
    private final ProjectRepository projectRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public StatusChangeSubscriptionServiceImplTest(StatusChangeSubscriptionServiceImpl service,
                                                   StatusChangeSubscriptionRepository statusChangeSubscriptionRepository,
                                                   ProjectRepository projectRepository, GroupRepository groupRepository) {
        this.service = service;
        this.statusChangeSubscriptionRepository = statusChangeSubscriptionRepository;
        this.projectRepository = projectRepository;
        this.groupRepository = groupRepository;
    }

    @Test
    void getSubscribedProjectIds() {
        var projectId = givenProject("project a");
        givenStatusChangeSubscriptionForProject(projectId);

        assertEquals(Set.of(projectId), service.getSubscribedProjectIds());
    }

    @Test
    void updateSubscriptions() {
        var projectIdA = givenProject("project a");
        var projectIdB = givenProject("project b");
        var projectIdC = givenProject("project c");
        var projectIdD = givenProject("project d");

        givenStatusChangeSubscriptionForProject(projectIdA);
        givenStatusChangeSubscriptionForProject(projectIdC);
        statusChangeSubscriptionRepository.flush();

        var result = service.updateSubscriptions(Set.of(projectIdA, projectIdB), Set.of(projectIdC, projectIdD));

        assertEquals(Set.of(projectIdC, projectIdD), result);
    }

    private void givenStatusChangeSubscriptionForProject(String projectId) {
        var statusChangeSubscription = new StatusChangeSubscription(null, projectRepository.findById(projectId).get());
        statusChangeSubscriptionRepository.saveAndFlush(statusChangeSubscription)
                .getId();
    }

    private String givenProject(String name) {
        var group = groupRepository.findByName("group")
                .orElseGet(() -> groupRepository.saveAndFlush(new Group(null, "group")));

        return projectRepository.saveAndFlush(new Project(null, name, Status.OK, group, null))
                .getId();
    }
}