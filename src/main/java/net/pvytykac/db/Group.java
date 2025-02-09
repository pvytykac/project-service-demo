package net.pvytykac.db;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "groups", uniqueConstraints = @UniqueConstraint(name = "uq_group", columnNames = "name"))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true, onConstructor_ = @PersistenceCreator)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;

}
