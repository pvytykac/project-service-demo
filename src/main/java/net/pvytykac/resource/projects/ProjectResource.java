package net.pvytykac.resource.projects;

import com.github.fge.jsonpatch.JsonPatch;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import net.pvytykac.db.Project;
import net.pvytykac.db.StatusOverride;
import net.pvytykac.resource.GenericListRepresentation;
import net.pvytykac.resource.groups.representations.GroupRepresentation;
import net.pvytykac.resource.projects.representations.ProjectRepresentation;
import net.pvytykac.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * @author Paly
 * @since 2025-02-07
 */
//todo: dump individual validation errors into the response body
@Slf4j
@RestController
@RequestMapping("/v1/projects")
public class ProjectResource {

    private final ProjectService service;

    @Autowired
    public ProjectResource(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    public GenericListRepresentation<ProjectRepresentation> listProjects() {
        return new GenericListRepresentation<>(service.listProjects().stream()
                .map(ProjectResource::entityToRepresentation)
                .toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectRepresentation postProject(@RequestBody @Validated @NotNull ProjectRepresentation payload) {
        return entityToRepresentation(service.createProject(payload));
    }

    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    public ProjectRepresentation patchProject(@PathVariable("id") String id, @RequestBody @NotNull JsonPatch payload) {
        return service.updateProject(id, payload)
                .map(ProjectResource::entityToRepresentation)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND.value(), null, null));
    }

    @DeleteMapping("/{id}")
    public ProjectRepresentation deleteProject(@PathVariable("id") String id) {
        return service.deleteProject(id)
                .map(ProjectResource::entityToRepresentation)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND.value(), null, null));
    }

    private static ProjectRepresentation entityToRepresentation(Project project) {
        return ProjectRepresentation.builder()
                .id(project.getId())
                .name(project.getName())
                .group(GroupRepresentation.builder()
                        .id(project.getGroup().getId())
                        .name(project.getGroup().getName())
                        .build())
                .reportedStatus(project.getStatus())
                .statusOverride(Optional.ofNullable(project.getStatusOverride()).map(StatusOverride::getStatus).orElse(null))
                .build();
    }
}
