package net.pvytykac.resource;

import net.pvytykac.db.repo.GroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author Paly
 * @since 2025-02-07
 */
//todo: add coverage
@SpringBootTest
@AutoConfigureMockMvc
public class GroupResourceTest {

    private final MockMvc mvc;

    @MockitoBean
    private GroupRepository repository;

    @Autowired
    public GroupResourceTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void listGroups() {

    }
}
