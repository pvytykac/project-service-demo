package net.pvytykac.service.impl;

import net.pvytykac.EmbeddedPostgresConfiguration;
import net.pvytykac.db.Group;
import net.pvytykac.db.Project;
import net.pvytykac.db.Status;
import net.pvytykac.db.StatusOverride;
import net.pvytykac.db.repo.GroupRepository;
import net.pvytykac.db.repo.ProjectRepository;
import net.pvytykac.resource.groups.representations.GroupRepresentation;
import net.pvytykac.resource.projects.representations.ProjectRepresentation;
import net.pvytykac.service.EntityDoesNotExistException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        var group = givenGroup();

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

    //todo: why doesn't this work ? embedded postgres not honoring unique constraints ?
//    @Test
//    void createNew_GroupNameClash() {
//        var group = givenGroup();
//
//        var representation = ProjectRepresentation.builder()
//                .name("Project A")
//                .reportedStatus(Status.OK)
//                .group(GroupRepresentation.builder().name(group.getName()).build())
//                .build();
//
//        assertThrows(DataIntegrityViolationException.class, () -> service.createProject(representation));
//    }

    @Test
    void listProjects_Empty() {
        assertEquals(List.of(), service.listProjects());
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

    @Test
    void updateProject_Existing_NameStatusGroupNameAndOverride() {
        var group = givenGroup();
        var project = givenProjectInGroup(group);
        var representation = ProjectRepresentation.builder()
                .name("edited project")
                .reportedStatus(Status.ERROR)
                .group(new GroupRepresentation(group.getId(), "edited group"))
                .statusOverride(Status.WARN)
                .build();

        var updated = service.updateProject(project.getId(), representation);

        assertTrue(updated.isPresent());
        assertEquals(project.getId(), updated.get().getId());
        assertEquals(representation.getName(), updated.get().getName());
        assertEquals(representation.getReportedStatus(), updated.get().getStatus());

        assertEquals(representation.getGroup().getId(), updated.get().getGroup().getId());
        assertEquals(representation.getGroup().getName(), updated.get().getGroup().getName());

        assertNotNull(updated.get().getStatusOverride());
        assertNotNull(updated.get().getStatusOverride().getId());
        assertEquals(representation.getStatusOverride(), updated.get().getStatusOverride().getStatus());
    }

    @Test
    void updateProject_Existing_GroupReassignedToNonExistentOne() {
        var group = givenGroup();
        var project = givenProjectInGroup(group);
        var representation = ProjectRepresentation.builder()
                .name(project.getName())
                .reportedStatus(project.getStatus())
                .group(new GroupRepresentation(UUID.randomUUID().toString(), "non-existent group"))
                .build();

        assertThrows(EntityDoesNotExistException.class, () -> service.updateProject(project.getId(), representation));
    }

    @Test
    void updateProject_Existing_GroupReassignedAndRenamed_StatusOverrideChanged() {
        var oldGroup = givenGroupWithName("old");
        var newGroup = givenGroupWithName("new");
        var project = givenProjectWithStatusOverrideInGroup(oldGroup, Status.WARN);

        var representation = ProjectRepresentation.builder()
                .name(project.getName())
                .reportedStatus(project.getStatus())
                .group(new GroupRepresentation(newGroup.getId(), "new edited"))
                .statusOverride(Status.ERROR)
                .build();

        var updated = service.updateProject(project.getId(), representation);

        assertTrue(updated.isPresent());
        assertEquals(project.getId(), updated.get().getId());
        assertEquals(representation.getName(), updated.get().getName());
        assertEquals(representation.getReportedStatus(), updated.get().getStatus());

        assertEquals(representation.getGroup().getId(), updated.get().getGroup().getId());
        assertEquals(representation.getGroup().getName(), updated.get().getGroup().getName());

        assertNotNull(updated.get().getStatusOverride());
        assertEquals(project.getStatusOverride().getId(), updated.get().getStatusOverride().getId());
        assertEquals(representation.getStatusOverride(), updated.get().getStatusOverride().getStatus());

        assertEquals("new edited", groupRepository.findById(newGroup.getId()).get().getName());
        assertEquals("old", groupRepository.findById(oldGroup.getId()).get().getName());
    }

    @Test
    void updateProject_Existing_GroupReassignedToNewGroup_OverrideRemoved() {
        var oldGroup = givenGroupWithName("old");
        var project = givenProjectWithStatusOverrideInGroup(oldGroup, Status.ERROR);

        var representation = ProjectRepresentation.builder()
                .name(project.getName())
                .reportedStatus(project.getStatus())
                .group(new GroupRepresentation(null, "new"))
                .build();

        var updated = service.updateProject(project.getId(), representation);

        assertTrue(updated.isPresent());
        assertNotNull(updated.get().getGroup().getId());
        assertNotEquals(oldGroup.getId(), updated.get().getGroup().getId());
        assertEquals(representation.getGroup().getName(), updated.get().getGroup().getName());

        assertNull(updated.get().getStatusOverride());
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
