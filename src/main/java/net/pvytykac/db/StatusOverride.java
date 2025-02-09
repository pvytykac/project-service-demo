package net.pvytykac.db;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceCreator;

/**
 * @author Paly
 * @since 2025-02-08
 */
@Entity
@Table(name = "project_status_overrides")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true, onConstructor_ = @PersistenceCreator)
public class StatusOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    Status status;

}
