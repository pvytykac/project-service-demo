package net.pvytykac.service.impl;

import net.pvytykac.EmbeddedPostgresConfiguration;
import net.pvytykac.db.Group;
import net.pvytykac.db.Status;
import net.pvytykac.db.repo.GroupRepository;
import net.pvytykac.resource.groups.representations.GroupRepresentation;
import net.pvytykac.resource.projects.representations.ProjectRepresentation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

/**
 * @author Paly
 * @since 2025-02-09
 */
//todo: add coverage
@DataJpaTest
@ExtendWith(EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = NONE)
@ContextConfiguration(classes = EmbeddedPostgresConfiguration.class)
@ComponentScan(basePackages = "net.pvytykac")
public class ProjectServiceImplTest {

    private final ProjectServiceImpl service;
    private final GroupRepository groupRepository;

    @Autowired
    public ProjectServiceImplTest(ProjectServiceImpl service, GroupRepository groupRepository) {
        this.service = service;
        this.groupRepository = groupRepository;
    }

    @Test
    void createNew_WithOverride_NewGroupCreated() {
        var representation = ProjectRepresentation.builder()
                .name("Project A")
                .reportedStatus(Status.OK)
                .group(GroupRepresentation.builder().name("Group A").build())
                .statusOverride(Status.ERROR)
                .build();

        var project = service.createProject(representation);

        assertNotNull(project.getId());
        assertEquals(representation.getName(), project.getName());
        assertEquals(representation.getReportedStatus(), project.getStatus());

        assertNotNull(project.getGroup().getId());
        assertEquals(representation.getGroup().getName(), project.getGroup().getName());

        assertNotNull(project.getStatusOverride().getId());
        assertEquals(representation.getStatusOverride(), project.getStatusOverride().getStatus());
    }

    @Test
    void createNew_NoOverride_ExistingGroupUpdated() {
        var group = Group.builder()
                .name("old-name")
                .build();
        groupRepository.save(group);

        var representation = ProjectRepresentation.builder()
                .name("Project A")
                .reportedStatus(Status.OK)
                .group(GroupRepresentation.builder().id(group.getId()).name("new-name").build())
                .build();

        var project = service.createProject(representation);

        assertNotNull(project.getId());
        assertEquals(representation.getName(), project.getName());
        assertEquals(representation.getReportedStatus(), project.getStatus());

        assertEquals(group.getId(), project.getGroup().getId());
        assertEquals(representation.getGroup().getName(), project.getGroup().getName());

        assertNull(project.getStatusOverride());
    }
}
