package net.pvytykac.db.repo;

import net.pvytykac.db.StatusChangeSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

/**
 * @author Paly
 * @since 2025-02-10
 */
public interface StatusChangeSubscriptionRepository extends JpaRepository<StatusChangeSubscription, String> {

    @Query("select s.project.id from StatusChangeSubscription s")
    Set<String> findAllSubscribedProjectIds();

    @Query("select s.project.id from StatusChangeSubscription s where s.project.id IN ?1")
    Set<String> findSubscribedProjectIds(Set<String> projectIds);

    @Modifying
    @Query("delete from StatusChangeSubscription s where s.project.id in ?1")
    void deleteByProjectIds(Set<String> projectIds);

}
