package net.pvytykac.resource;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.pvytykac.db.Project;
import net.pvytykac.db.repo.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author Paly
 * @since 2025-02-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/projects")
@Transactional
public class ProjectResource {

    private final ProjectRepository repository;

    @Autowired
    public ProjectResource(ProjectRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Project> listProjects() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Project> getProject(@PathVariable("id") String id) {
        return repository.findById(id);
    }

    @PostMapping
    public void postProject(@RequestBody @Validated Project project) {
        repository.save(project);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putProject(@PathVariable("id") String id, @RequestBody @Validated Project project) {
        repository.save(Project.builder()
                .id(id)
                .name(project.getName())
                .build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable("id") String id) {
        repository.deleteById(id);
    }

//    @Value
//    @Builder
//    @AllArgsConstructor(access = AccessLevel.PRIVATE)
//    public static class GetProjectsPayload {
//        List<Project> projects;
//    }
//
//    @Value
//    @Builder
//    @AllArgsConstructor(access = AccessLevel.PRIVATE)
//    public static class Project {
//        int id;
//        String name;
//    }
}
