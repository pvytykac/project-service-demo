package net.pvytykac.service.impl;

import net.pvytykac.EmbeddedPostgresConfiguration;
import net.pvytykac.db.Group;
import net.pvytykac.db.Project;
import net.pvytykac.db.Status;
import net.pvytykac.db.StatusOverride;
import net.pvytykac.db.repo.GroupRepository;
import net.pvytykac.db.repo.ProjectRepository;
import net.pvytykac.resource.projects.representations.ProjectRepresentation;
import net.pvytykac.resource.projects.representations.StatusRepresentation;
import net.pvytykac.service.EntityDoesNotExistException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED;

/**
 * @author Paly
 * @since 2025-02-09
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AUTO_CONFIGURED)
@ContextConfiguration(classes = EmbeddedPostgresConfiguration.class)
@ComponentScan(basePackages = "net.pvytykac")
public class ProjectServiceImplTest {

    private final ProjectServiceImpl service;
    private final ProjectRepository projectRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public ProjectServiceImplTest(ProjectServiceImpl service, ProjectRepository projectRepository,
                                  GroupRepository groupRepository) {
        this.service = service;
        this.projectRepository = projectRepository;
        this.groupRepository = groupRepository;
    }

    @Test
    void listProjects() {
        var group = givenGroup();
        var project = givenProjectInGroup(group);

        assertEquals(List.of(project), service.listProjects(group.getId()));
        assertEquals(List.of(), service.listProjects("non-existent-id"));
    }

    @Test
    void createNew_GroupNotExists() {
        var representation = ProjectRepresentation.builder()
                .name("Project A")
                .status(StatusRepresentation.builder()
                        .reportedStatus(Status.OK)
                        .build())
                .groupId(UUID.randomUUID().toString())
                .build();

        assertThrows(EntityDoesNotExistException.class, () -> service.createProject(representation));
    }

    @Test
    void createNew_ExistingGroup() {
        var group = givenGroup();

        var representation = ProjectRepresentation.builder()
                .name("Project A")
                .status(StatusRepresentation.builder()
                        .reportedStatus(Status.OK)
                        .overriddenStatus(Status.ERROR)
                        .build())
                .groupId(group.getId())
                .build();

        var project = service.createProject(representation);

        assertNotNull(project.getId());
        assertEquals(representation.getName(), project.getName());
        assertEquals(representation.getStatus().getReportedStatus(), project.getStatus());

        assertNotNull(project.getStatusOverride());
        assertEquals(representation.getStatus().getOverriddenStatus(), project.getStatusOverride().getStatus());

        assertEquals(group.getId(), project.getGroup().getId());
    }

    @Test
    void update_ExistingGroup_NullToSomeOverride() {
        var oldGroup = givenGroup();
        var newGroup = givenGroup();
        var project = givenProjectInGroup(oldGroup);

        var representation = ProjectRepresentation.builder()
                .name("new-name")
                .groupId(newGroup.getId())
                .status(StatusRepresentation.builder()
                        .reportedStatus(Status.WARN)
                        .overriddenStatus(Status.ERROR)
                        .build())
                .build();

        var updated = service.updateProject(project.getId(), representation);

        assertTrue(updated.isPresent());
        assertEquals(project.getId(), updated.get().getId());
        assertEquals(representation.getName(), updated.get().getName());
        assertEquals(representation.getStatus().getReportedStatus(), updated.get().getStatus());

        assertNotNull(updated.get().getStatusOverride());
        assertNotNull(representation.getStatus().getOverriddenStatus(), updated.get().getStatusOverride().getId());
        assertEquals(representation.getStatus().getOverriddenStatus(), updated.get().getStatusOverride().getStatus());
    }

    @Test
    void update_SomeToNullOverride() {
        var group = givenGroup();
        var project = givenProjectWithStatusOverrideInGroup(group, Status.ERROR);

        var representation = ProjectRepresentation.builder()
                .name(project.getName())
                .groupId(group.getId())
                .status(StatusRepresentation.builder()
                        .reportedStatus(project.getStatus())
                        .build())
                .build();

        var updated = service.updateProject(project.getId(), representation);

        assertTrue(updated.isPresent());
        assertEquals(project.getId(), updated.get().getId());
        assertNull(updated.get().getStatusOverride());
    }

    @Test
    void update_SomeToAnotherOverride() {
        var group = givenGroup();
        var project = givenProjectWithStatusOverrideInGroup(group, Status.ERROR);
        var statusOverrideId = project.getStatusOverride().getId();

        var representation = ProjectRepresentation.builder()
                .name(project.getName())
                .groupId(group.getId())
                .status(StatusRepresentation.builder()
                        .reportedStatus(project.getStatus())
                        .overriddenStatus(Status.OK)
                        .build())
                .build();

        var updated = service.updateProject(project.getId(), representation);

        assertTrue(updated.isPresent());
        assertEquals(project.getId(), updated.get().getId());
        assertNotNull(updated.get().getStatusOverride());

        assertEquals(statusOverrideId, updated.get().getStatusOverride().getId());
        assertEquals(representation.getStatus().getOverriddenStatus(), updated.get().getStatusOverride().getStatus());
    }

    @Test
    void update_NonExistentGroup() {
        var group = givenGroup();
        var project = givenProjectInGroup(group);

        var representation = ProjectRepresentation.builder()
                .name(project.getName())
                .groupId(UUID.randomUUID().toString())
                .status(StatusRepresentation.builder()
                        .reportedStatus(project.getStatus())
                        .build())
                .build();

        assertThrows(EntityDoesNotExistException.class, () -> service.updateProject(project.getId(), representation));
    }

    @Test
    void deleteProject_NotFound() {
        assertEquals(Optional.empty(), service.deleteProject(UUID.randomUUID().toString()));
    }

    @Test
    void deleteProject_Existing() {
        var group = givenGroup();
        var project = givenProjectInGroup(group);

        assertEquals(Optional.of(project), service.deleteProject(project.getId()));

        assertEquals(Optional.empty(), projectRepository.findById(project.getId()));
    }

    private Group givenGroup() {
        return givenGroupWithName("group");
    }

    private Group givenGroupWithName(String name) {
        return groupRepository.save(new Group(null, name));
    }

    private Project givenProjectInGroup(Group group) {
        return givenProjectWithStatusOverrideInGroup(group, null);
    }

    private Project givenProjectWithStatusOverrideInGroup(Group group, Status overridenStatus) {
        var override = Optional.ofNullable(overridenStatus)
                .map(status -> new StatusOverride(null, status))
                .orElse(null);

        return projectRepository.save(new Project(null, "project", Status.OK, group, override));
    }
}
