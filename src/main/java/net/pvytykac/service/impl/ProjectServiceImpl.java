package net.pvytykac.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.pvytykac.db.Group;
import net.pvytykac.db.Project;
import net.pvytykac.db.Status;
import net.pvytykac.db.StatusOverride;
import net.pvytykac.db.repo.GroupRepository;
import net.pvytykac.db.repo.ProjectRepository;
import net.pvytykac.resource.projects.representations.ProjectRepresentation;
import net.pvytykac.service.EntityDoesNotExistException;
import net.pvytykac.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Paly
 * @since 2025-02-09
 */
//todo: detach dbo's before returning - as they are not immutable anymore :.(
@Slf4j
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, GroupRepository groupRepository) {
        this.projectRepository = projectRepository;
        this.groupRepository = groupRepository;
    }

    @Override
    public List<Project> listProjects(String groupId) {
        log.debug("listing projects");

        return projectRepository.findByGroupId(groupId);
    }

    @Override
    public Project createProject(ProjectRepresentation representation) {
        log.info("creating new project '{}'", representation.getName());

        var project = Project.builder()
                .name(representation.getName())
                .status(representation.getStatus().getReportedStatus())
                .group(getGroupOrThrow(representation.getGroupId()))
                .statusOverride(Optional.ofNullable(representation.getStatus().getOverriddenStatus())
                        .map(override -> StatusOverride.builder()
                                .status(override)
                                .build())
                        .orElse(null))
                .build();

        return projectRepository.save(project);
    }

    @Override
    public Optional<Project> updateProject(String id, ProjectRepresentation representation) {
        log.info("updating project '{}'", id);

        return projectRepository.findById(id)
                .map(p -> updateProjectFields(p, representation))
                .map(projectRepository::save);
    }

    @Override
    public Optional<Project> deleteProject(String id) {
        log.info("deleting project '{}'", id);

        var project = projectRepository.findById(id);
        project.ifPresent(projectRepository::delete);

        return project;
    }

    private Project updateProjectFields(Project project, ProjectRepresentation representation) {
        project.setName(representation.getName());
        project.setStatus(representation.getStatus().getReportedStatus());
        project.setStatusOverride(updateStatusOverride(project.getStatusOverride(), representation.getStatus().getOverriddenStatus()));
        project.setGroup(getGroupOrThrow(representation.getGroupId()));

        return project;
    }

    private StatusOverride updateStatusOverride(StatusOverride override, Status status) {
        if (status == null) {
            return null;
        }

        var statusOverride = Optional.ofNullable(override)
                .orElse(new StatusOverride());
        statusOverride.setStatus(status);

        return statusOverride;
    }

    private Group getGroupOrThrow(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityDoesNotExistException("group", groupId));
    }
}
