package net.pvytykac.db;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.data.annotation.PersistenceCreator;

/**
 * @author Paly
 * @since 2025-02-07
 */
@Entity
@Table(name = "projects")
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = @PersistenceCreator)
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String name;

}
