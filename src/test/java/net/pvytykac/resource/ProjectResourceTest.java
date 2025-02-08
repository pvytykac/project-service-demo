package net.pvytykac.resource;

import net.pvytykac.db.repo.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Paly
 * @since 2025-02-07
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectResourceTest {

    private final MockMvc mvc;

    @MockitoBean
    private ProjectRepository repository;

    @Autowired
    public ProjectResourceTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void listProjects() throws Exception {
        when(repository.findAll()).thenReturn(ImmutableList.of());

        mvc.perform(get("/v1/projects").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(repository).findAll();
    }

    @Test
    void deleteProjects() throws Exception {
        var id = UUID.randomUUID().toString();

        mvc.perform(delete("/v1/projects/" + id))
                .andExpect(status().isNoContent());

        verify(repository).deleteById(id);
    }
}
