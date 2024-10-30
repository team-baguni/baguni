package kernel360.techpick.feature.api.pick.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kernel360.techpick.feature.domain.link.dto.LinkInfo;

public class PickApiRequest {

	public record Create(
		@Schema(example = "Record란?") String title,
		@Schema(example = "Java 레코드에 관한 글") String memo,
		@Schema(example = "[4, 5, 2, 1, 3]") List<Long> tagOrderList,
		@Schema(example = "1") Long parentFolderId,
		LinkInfo linkInfo
	) {
	}

	public record Read(
		@Schema(example = "1") @NotNull Long pickId
	) {
	}

	public record Fetch(
		@Schema(example = "[1, 2, 3]") @NotNull List<Long> folderIdList,
		@Schema(example = "[\"리액트\", \"쿼리\", \"서버\"]") @NotNull List<String> searchTokenList
	) {
	}

	public record Update(
		@Schema(example = "1") @NotNull Long pickId,
		@Schema(example = "Record란 뭘까?") String title,
		@Schema(example = "Java Record") String memo,
		@Schema(example = "[4, 5, 2, 1]") List<Long> tagIdList
	) {
	}

	public record Move(
		@Schema(example = "[1, 2]") @NotNull List<Long> pickIdList,
		@Schema(example = "3") @NotNull Long destinationFolderId,
		@Schema(example = "0") int orderIdx
	) {
	}

	public record Delete(
		@Schema(example = "[1]") @NotNull List<Long> pickIdList
	) {
	}
}
