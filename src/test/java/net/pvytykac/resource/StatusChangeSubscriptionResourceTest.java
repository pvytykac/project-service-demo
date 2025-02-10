package net.pvytykac.resource;

import net.pvytykac.service.StatusChangeSubscriptionService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Paly
 * @since 2025-02-10
 */
@SpringBootTest
@AutoConfigureMockMvc
class StatusChangeSubscriptionResourceTest {

    private final MockMvc mvc;

    @MockitoBean
    private StatusChangeSubscriptionService service;

    @Autowired
    public StatusChangeSubscriptionResourceTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void getStatusChangeSubscriptions() throws Exception {
        var projectId = UUID.randomUUID().toString();
        when(service.getSubscribedProjectIds()).thenReturn(Set.of(projectId));

        mvc.perform(get("/v1/me/status-change-subscriptions").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(new JSONObject().put("items", new JSONArray().put(projectId)).toString()));
    }

    @Test
    void patchStatusChangeSubscriptions() throws Exception {
        var subscribedProject = UUID.randomUUID().toString();
        var unsubscribedProject = UUID.randomUUID().toString();
        var json = new JSONObject()
                .put("subscribeTo", new JSONArray().put(subscribedProject))
                .put("unsubscribeFrom", new JSONArray().put(unsubscribedProject));

        when(service.updateSubscriptions(any(), any())).thenReturn(Set.of(subscribedProject));

        mvc.perform(patch("/v1/me/status-change-subscriptions").contentType(MediaType.APPLICATION_JSON).content(json.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(new JSONObject().put("items", new JSONArray().put(subscribedProject)).toString()));

        verify(service).updateSubscriptions(Set.of(unsubscribedProject), Set.of(subscribedProject));
    }
}