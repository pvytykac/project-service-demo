package net.pvytykac.db.repo;

import net.pvytykac.db.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * @author Paly
 * @since 2025-02-07
 */
public interface ProjectRepository extends JpaRepository<Project, String> {

    @Query("select p from Project p where p.id in ?1")
    Set<Project> findProjectsByIds(Set<String> projectIds);

    @Query("select p from Project p where p.group.id = ?1")
    List<Project> findByGroupId(String groupId);
}
