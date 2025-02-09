package net.pvytykac.service;

import com.github.fge.jsonpatch.JsonPatch;
import net.pvytykac.db.Project;
import net.pvytykac.resource.projects.representations.ProjectRepresentation;

import java.util.List;
import java.util.Optional;

/**
 * @author Paly
 * @since 2025-02-09
 */
public interface ProjectService {

    List<Project> listProjects();
    Project createProject(ProjectRepresentation project);
    Optional<Project> updateProject(String id, JsonPatch update);
    Optional<Project> deleteProject(String id);
}
