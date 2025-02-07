package net.pvytykac.resource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Paly
 * @since 2025-02-07
 */
@RestController
@RequestMapping("/v1/projects")
public class ProjectResource {

    @GetMapping
    public GetProjectsPayload listProjects() {
        return new GetProjectsPayload(List.of(new Project(1, "Project A"), new Project(2, "Project B")));
    }

    @Value
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class GetProjectsPayload {
        List<Project> projects;
    }

    @Value
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Project {
        int id;
        String name;
    }
}
