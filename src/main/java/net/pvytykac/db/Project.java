package net.pvytykac.db;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.PersistenceCreator;

/**
 * @author Paly
 * @since 2025-02-07
 */
@Entity
@Table(name = "projects", uniqueConstraints = @UniqueConstraint(name = "uq_project", columnNames = {"name", "group_id"}))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true, onConstructor_ = @PersistenceCreator)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;
    Status status;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    Group group;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "status_override_id", referencedColumnName = "id")
    StatusOverride statusOverride;

}
