package net.pvytykac.db.repo;

import net.pvytykac.db.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author Paly
 * @since 2025-02-08
 */
public interface GroupRepository extends JpaRepository<Group, String> {

    @Query("select g from Group g where g.name = ?1")
    Optional<Group> findByName(String name);

}
