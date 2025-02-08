package net.pvytykac.db.repo;

import net.pvytykac.EmbeddedPostgresConfiguration;
import net.pvytykac.db.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Paly
 * @since 2025-02-07
 */
@DataJpaTest
@ExtendWith(EmbeddedPostgresConfiguration.EmbeddedPostgresExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {EmbeddedPostgresConfiguration.class})
@ActiveProfiles("test")
class ProjectRepositoryTest {

    private final ProjectRepository repository;

    @Autowired
    public ProjectRepositoryTest(ProjectRepository repository) {
        this.repository = repository;
    }

    @Test
    void saveAndFindById() {
        var project = Project.builder()
                .name("Project A")
                .build();

        String id = repository.save(project).getId();
        assertNotNull(id);

        Optional<Project> lookedUp = repository.findById(id);
        assertTrue(lookedUp.isPresent());
        assertEquals(project, lookedUp.get());
    }
}