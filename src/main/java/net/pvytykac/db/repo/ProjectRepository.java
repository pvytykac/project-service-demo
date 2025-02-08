package net.pvytykac.db.repo;

import net.pvytykac.db.Project;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Paly
 * @since 2025-02-07
 */
public interface ProjectRepository extends JpaRepository<Project, String> {
}
