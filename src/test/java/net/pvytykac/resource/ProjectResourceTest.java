package net.pvytykac.resource;

import net.pvytykac.db.Group;
import net.pvytykac.db.Project;
import net.pvytykac.db.Status;
import net.pvytykac.db.StatusOverride;
import net.pvytykac.service.EntityDoesNotExistException;
import net.pvytykac.service.ProjectService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Paly
 * @since 2025-02-07
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProjectResourceTest {

    private static final String GROUP_ID = UUID.randomUUID().toString();
    private static final String PROJECT_ID = UUID.randomUUID().toString();
    private static final Project PROJECT_OBJECT = Project.builder()
            .id(PROJECT_ID)
            .name("Project A")
            .status(Status.OK)
            .group(Group.builder()
                    .id(GROUP_ID)
                    .name("Group A")
                    .build())
            .statusOverride(StatusOverride.builder()
                    .id(UUID.randomUUID().toString())
                    .status(Status.ERROR)
                    .build())
            .build();

    private final MockMvc mvc;

    @MockitoBean
    private ProjectService service;

    @Autowired
    public ProjectResourceTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void listProjects_NoGroupId() throws Exception {
        mvc.perform(get("/v1/projects").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listProjects_SomeOverride() throws Exception {
        when(service.listProjects(GROUP_ID)).thenReturn(ImmutableList.of(PROJECT_OBJECT));

        mvc.perform(get("/v1/projects?groupId=" + GROUP_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id", equalTo(PROJECT_ID)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name", equalTo(PROJECT_OBJECT.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].groupId", equalTo(PROJECT_OBJECT.getGroup().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].status.reportedStatus", equalTo(PROJECT_OBJECT.getStatus().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].status.overriddenStatus", equalTo(PROJECT_OBJECT.getStatusOverride().getStatus().toString())));
    }

    @Test
    void postProject_Created() throws Exception {
        when(service.createProject(any())).thenReturn(PROJECT_OBJECT);

        mvc.perform(post("/v1/projects").contentType(MediaType.APPLICATION_JSON).content(getRawProjectJson().toString()))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(PROJECT_ID)));
    }

    @Test
    void postProject_Conflict() throws Exception {
        doThrow(new DataIntegrityViolationException("mock exception")).when(service).createProject(any());

        mvc.perform(post("/v1/projects").contentType(MediaType.APPLICATION_JSON).content(getRawProjectJson().toString()))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", equalTo("unique constraint violation: 'mock exception'")));
    }

    @Test
    void putProject_ProjectNotFound() throws Exception {
        when(service.updateProject(eq(PROJECT_ID), any())).thenReturn(Optional.empty());

        mvc.perform(put("/v1/projects/" + PROJECT_ID).contentType(MediaType.APPLICATION_JSON)
                .content(getRawProjectJson().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void putProject_GroupNotFound() throws Exception {
        doThrow(new EntityDoesNotExistException("group", "id")).when(service).updateProject(eq(PROJECT_ID), any());

        mvc.perform(put("/v1/projects/" + PROJECT_ID).contentType(MediaType.APPLICATION_JSON)
                .content(getRawProjectJson().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void putProject_Ok() throws Exception {
        when(service.updateProject(eq(PROJECT_ID), any())).thenReturn(Optional.of(PROJECT_OBJECT));

        mvc.perform(put("/v1/projects/" + PROJECT_ID).contentType(MediaType.APPLICATION_JSON)
                .content(getRawProjectJson().toString()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", equalTo(PROJECT_ID)));
    }

    @Test
    void deleteProjects_Ok() throws Exception {
        var id = UUID.randomUUID().toString();
        when(service.deleteProject(id)).thenReturn(Optional.of(PROJECT_OBJECT));

        mvc.perform(delete("/v1/projects/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProjects_NotFound() throws Exception {
        var id = UUID.randomUUID().toString();
        when(service.deleteProject(id)).thenReturn(Optional.empty());

        mvc.perform(delete("/v1/projects/" + id))
                .andExpect(status().isNotFound());
    }

    private static JSONObject getRawProjectJson() throws Exception {
        return new JSONObject()
                .put("id", PROJECT_ID)
                .put("name", PROJECT_OBJECT.getName())
                .put("groupId", PROJECT_OBJECT.getGroup().getId())
                .put("status", new JSONObject()
                        .put("reportedStatus", PROJECT_OBJECT.getStatus().toString())
                        .put("overriddenStatus", PROJECT_OBJECT.getStatusOverride().getStatus().toString()));
    }
}
