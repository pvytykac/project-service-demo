package net.pvytykac.resource.projects.representations;

import net.pvytykac.db.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Paly
 * @since 2025-02-09
 */
class ProjectRepresentationTest {

    @Test
    void getEffectiveStatus_Override() {
        assertEquals(Status.OK, ProjectRepresentation.builder()
                .reportedStatus(Status.WARN)
                .statusOverride(Status.OK)
                .build()
                .getEffectiveStatus());
    }

    @Test
    void getEffectiveStatus_Reported() {
        assertEquals(Status.WARN, ProjectRepresentation.builder()
                .reportedStatus(Status.WARN)
                .build()
                .getEffectiveStatus());
    }
}