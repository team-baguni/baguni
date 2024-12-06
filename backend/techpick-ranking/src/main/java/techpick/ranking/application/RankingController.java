package techpick.ranking.application;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import techpick.ranking.domain.pick.PickRankingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rank")
@Tag(name = "Rank API", description = "랭킹 API")
public class RankingController {

	private final PickRankingService pickRankingService;

	@GetMapping("/link_view")
	@Operation(summary = "링크 조회수 기반 Top N{:limit}개 조회", description = "조회수가 높은 순부터 N{:limit} 개를 반환합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "조회수 랭킹 획득 성공"),
		@ApiResponse(responseCode = "404", description = "유효하지 않는 날짜입니다.")
	})
	public ResponseEntity<?> getLinkRank(
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date_begin,
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date_end,
		@RequestParam(required = false, defaultValue = "5") Integer limit
	) {
		var result = pickRankingService.getLinksOrderByViewCount(date_begin, date_end, limit);
		return ResponseEntity.ok(result);
	}
}
