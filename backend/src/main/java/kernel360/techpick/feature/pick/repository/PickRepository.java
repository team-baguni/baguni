package kernel360.techpick.feature.pick.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kernel360.techpick.core.model.link.Link;
import kernel360.techpick.core.model.pick.Pick;

@Repository
public interface PickRepository extends JpaRepository<Pick, Long> {
	
	boolean existsByLink(Link link);
}