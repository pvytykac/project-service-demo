package net.pvytykac.resource.groups;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import net.pvytykac.db.Group;
import net.pvytykac.db.repo.GroupRepository;
import net.pvytykac.resource.GenericListRepresentation;
import net.pvytykac.resource.groups.representations.GroupRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * @author Paly
 * @since 2025-02-08
 */
@Slf4j
@RestController
@RequestMapping("/v1/groups")
public class GroupResource {

    private final GroupRepository repository;

    @Autowired
    public GroupResource(GroupRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public GenericListRepresentation<GroupRepresentation> listGroups() {
        log.debug("listing groups");

        return new GenericListRepresentation<>(repository.findAll().stream()
                .map(GroupResource::entityToRepresentation)
                .toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupRepresentation postGroup(@RequestBody @Valid @NotNull GroupRepresentation payload) {
        log.info("creating group '{}'", payload.getName());

        Group saved = repository.save(representationToEntity(null, payload));

        return entityToRepresentation(saved);
    }

    @GetMapping("/{id}")
    public GroupRepresentation getGroup(@PathVariable("id") String id) {
        log.debug("fetching group '{}'", id);

        return repository.findById(id)
                .map(GroupResource::entityToRepresentation)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND.value(), null, null));
    }

    @PutMapping("/{id}")
    public GroupRepresentation putGroup(@PathVariable("id") String id, @RequestBody @Valid @NotNull GroupRepresentation payload) {
        log.info("updating group '{}'", id);

        Optional<Group> group = repository.findById(id);
        group.ifPresent(g -> g.setName(payload.getName()));

        return group.map(GroupResource::entityToRepresentation)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND.value(), null, null));
    }

    @DeleteMapping("/{id}")
    public GroupRepresentation deleteGroup(@PathVariable("id") String id) {
        log.info("deleting group '{}'", id);

        var group = repository.findById(id);
        group.ifPresent(repository::delete);

        return group.map(GroupResource::entityToRepresentation)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND.value(), null, null));
    }

    private static Group representationToEntity(String id, GroupRepresentation representation) {
        return Group.builder()
                .id(id)
                .name(representation.getName())
                .build();
    }

    private static GroupRepresentation entityToRepresentation(Group entity) {
        return GroupRepresentation.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
