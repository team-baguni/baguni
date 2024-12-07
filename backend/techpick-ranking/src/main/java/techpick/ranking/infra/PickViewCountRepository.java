package techpick.ranking.infra;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickViewCountRepository extends MongoRepository<PickViewCount, String> {

	Optional<PickViewCount> findLinkViewCountByDateAndUrl(LocalDate date, String url);

	List<PickViewCount> findByDateBetween(
		LocalDate startDate,
		LocalDate endDate
	);
}
