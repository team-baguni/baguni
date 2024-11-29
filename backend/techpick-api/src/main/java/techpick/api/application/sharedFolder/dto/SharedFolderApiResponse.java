package techpick.api.application.sharedFolder.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import techpick.api.domain.sharedFolder.dto.SharedFolderResult;

public class SharedFolderApiResponse {

    public record Create(
        @Schema(description = "{shared.accessToken.description}", example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d")
        @NotNull(message = "{shared.accessToken.notNull}")
        String accessToken
    ) {
    }

    /**
     * @author minkyeu kim
     * 내 공유 폴더 목록 획득시 사용되는 DTO.
     * 내부 픽 목록, 태그 조회 등을 하지 않는다.
     */
    public record ReadFolderPartial(
        @Schema(description = "원본 폴더의 이름", example = "리액트 모음집")
        @NotBlank(message = "{folder.name.notBlank}")
        String folderName,

        @Schema(description = "원본 폴더의 생성 시점", example = "2024-11-29T06:03:49.182Z")
        LocalDateTime createdAt,

        @Schema(description = "원본 폴더의 마지막 업데이트 시점", example = "2024-11-29T06:03:49.182Z")
        LocalDateTime updatedAt,

        @Schema(description = "{shared.accessToken.description}", example = "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d")
        @NotNull(message = "{shared.accessToken.notNull}")
        String accessToken
    ) {
    }

    /**
     * @author minkyeu kim
     * 외부에서 공유 폴더 접근 시, 모든 정보를 내려줘야 한 페이지로 그려낼 수 있다.
     * 이를 위해서 DB에서 모든 픽, 태그 정보를 반환한다.
     */
    public record ReadFolderFull(
        @Schema(description = "원본 폴더의 이름", example = "리액트 모음집")
        @NotBlank(message = "{folder.name.notBlank}")
        String folderName,

        @Schema(description = "원본 폴더의 생성 시점", example = "2024-11-29T06:03:49.182Z")
        LocalDateTime createdAt,

        @Schema(description = "원본 폴더의 마지막 업데이트 시점", example = "2024-11-29T06:03:49.182Z")
        LocalDateTime updatedAt,

        @Schema(
            description = "사용된 pick의 key 리스트. key는 tagIdMap에서 조회할 수 있습니다."
        )
        List<SharedFolderResult.SharedPickInfo> pickList,

        @Schema(
            description = "해당 폴더 내에서 사용된 모든 태그 정보와 대응하는 key 쌍",
            example = """
                {
                    "eedb9e2b-7faf-43f7-a199-ba3ba6732691" : { "name": "리액트", "colorNumber": "2" },
                    "06566296-ee25-4c1d-81e7-3f320fecf2a1" : { "name": "CSS", "colorNumber": "8" },
                }
                """
        )
        Map<UUID, SharedFolderResult.SharedTagInfo> tagIdMap
    ) {
    }
}