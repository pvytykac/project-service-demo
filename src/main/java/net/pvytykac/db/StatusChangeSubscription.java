package net.pvytykac.db;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceCreator;

/**
 * @author Paly
 * @since 2025-02-09
 */
@Entity
@Table(name = "status_change_subscriptions")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true, onConstructor_ = @PersistenceCreator)
public class StatusChangeSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String userId;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    Project project;
}
