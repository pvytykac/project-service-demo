package net.pvytykac.resource;

import net.pvytykac.db.Group;
import net.pvytykac.db.repo.GroupRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Paly
 * @since 2025-02-07
 */
@SpringBootTest
@AutoConfigureMockMvc
public class GroupResourceTest {

    private static final String GROUP_ID = UUID.randomUUID().toString();
    private static final Group GROUP_OBJECT = new Group(GROUP_ID, "Group A");

    private final MockMvc mvc;

    @MockitoBean
    private GroupRepository repository;

    @Autowired
    public GroupResourceTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void listGroups() throws Exception {
        when(repository.findAll()).thenReturn(List.of(GROUP_OBJECT));

        mvc.perform(get("/v1/groups").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id", equalTo(GROUP_ID)))
                .andExpect(jsonPath("$.items[0].name", equalTo(GROUP_OBJECT.getName())));
    }

    @Test
    void getGroup_NotFound() throws Exception {
        when(repository.findById(GROUP_ID)).thenReturn(Optional.empty());

        mvc.perform(get("/v1/groups/" + GROUP_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getGroup_Ok() throws Exception {
        when(repository.findById(GROUP_ID)).thenReturn(Optional.of(GROUP_OBJECT));

        mvc.perform(get("/v1/groups/" + GROUP_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(GROUP_ID)))
                .andExpect(jsonPath("$.name", equalTo(GROUP_OBJECT.getName())));
    }

    @Test
    void postGroup_Created() throws Exception {
        when(repository.save(any())).thenReturn(GROUP_OBJECT);

        mvc.perform(post("/v1/groups").contentType(MediaType.APPLICATION_JSON).content(getRawGroupJson().toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(GROUP_ID)));
    }

    @Test
    void postGroup_ValidationErrors() throws Exception {
        mvc.perform(post("/v1/groups").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'name')].message", equalTo(singletonList("must not be blank"))));
    }

    @Test
    void putGroup_NotFound() throws Exception {
        when(repository.findById(GROUP_ID)).thenReturn(Optional.empty());

        mvc.perform(put("/v1/groups/" + GROUP_ID).contentType(MediaType.APPLICATION_JSON).content(getRawGroupJson().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void putGroup_Ok() throws Exception {
        when(repository.findById(GROUP_ID)).thenReturn(Optional.of(GROUP_OBJECT));
        when(repository.save(GROUP_OBJECT)).thenReturn(GROUP_OBJECT);

        mvc.perform(put("/v1/groups/" + GROUP_ID).contentType(MediaType.APPLICATION_JSON).content(getRawGroupJson().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(GROUP_ID)));
    }

    @Test
    void deleteGroup_NotFound() throws Exception {
        when(repository.findById(GROUP_ID)).thenReturn(Optional.empty());

        mvc.perform(delete("/v1/groups/" + GROUP_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteGroup_Ok() throws Exception {
        when(repository.findById(GROUP_ID)).thenReturn(Optional.of(GROUP_OBJECT));

        mvc.perform(delete("/v1/groups/" + GROUP_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(GROUP_ID)));

        verify(repository).delete(GROUP_OBJECT);
    }

    private JSONObject getRawGroupJson() throws Exception {
        return new JSONObject()
                .put("id", GROUP_ID)
                .put("name", GROUP_OBJECT.getName());
    }
}
