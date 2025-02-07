package net.pvytykac.resource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    @Autowired
    public ProjectResourceTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void listProjects() throws Exception {
        var json = new JSONObject()
                .put("projects", new JSONArray()
                        .put(new JSONObject()
                                .put("id", 1)
                                .put("name", "Project A"))
                        .put(new JSONObject()
                                .put("id", 2)
                                .put("name", "Project B")));

        mvc.perform(get("/v1/projects").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(json.toString()));
    }
}
