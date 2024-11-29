package techpick.api.application.sharedFolder.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import techpick.api.application.folder.controller.FolderApiConstant;
import techpick.api.application.sharedFolder.dto.SharedFolderApiMapper;
import techpick.api.application.sharedFolder.dto.SharedFolderApiRequest;
import techpick.api.application.sharedFolder.dto.SharedFolderApiResponse;
import techpick.api.domain.pick.service.PickService;
import techpick.api.domain.sharedFolder.dto.SharedFolderCommand;
import techpick.api.domain.sharedFolder.dto.SharedFolderResult;
import techpick.api.domain.sharedFolder.service.SharedFolderService;
import techpick.security.annotation.LoginUserId;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shared")
public class SharedFolderApiController {

    private final SharedFolderService sharedFolderService;
    private final SharedFolderApiMapper sharedFolderApiMapper;

    @PostMapping
    @Operation(summary = "내 폴더를 공유 폴더로 등록", description = "폴더를 공유 폴더로 등록하며, 공유된 폴더는 부여된 UUID를 통해 접근할 수 있습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공유 폴더 생성 성공"),
        @ApiResponse(responseCode = "403", description = "자신의 폴더만 공유할 수 있습니다.")
    })
    public ResponseEntity<SharedFolderApiResponse.Create> createSharedFolder(
        @LoginUserId Long userId,
        @Valid @RequestBody SharedFolderApiRequest.Create request
    ) {
        var command = sharedFolderApiMapper.toCreateCommand(userId, request);
        var result = sharedFolderService.createSharedFolder(command);
        var response = sharedFolderApiMapper.toCreateResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "공유 폴더 조회", description = "UUID를 통해 공유된 폴더에 접근할 수 있습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공유 폴더 조회 성공"),
        @ApiResponse(responseCode = "404", description = "올바르지 않은 UUID", content = @Content),
    })
    public ResponseEntity<SharedFolderApiResponse.ReadFolderFull> getSharedFolderWithFullInfo(@PathVariable UUID uuid) {
        var result = sharedFolderService.getSharedFolderInfo(uuid);
        var response = sharedFolderApiMapper.toReadFolderFullResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "자신이 공유한 폴더 목록 조회", description = "자신이 공유한 폴더 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공유 폴더 목록 조회 성공")
    })
    public ResponseEntity<List<SharedFolderApiResponse.ReadFolderPartial>> getUserSharedFolderList(
        @LoginUserId Long userId) {
        var result = sharedFolderService.getSharedFolderListByUserId(userId);
        var response = sharedFolderApiMapper.toReadResponseList(result);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "공유 폴더 삭제", description = "공유폴더의 공유를 취소합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공유폴더 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "자신의 공유 폴더만 삭제 할 수 있습니다.")
    })
    public ResponseEntity<Void> deleteSharedFolder(@LoginUserId Long userId, @PathVariable UUID uuid) {
        sharedFolderService.deleteSharedFolder(new SharedFolderCommand.Delete(userId, uuid));
        return ResponseEntity.noContent().build();
    }
}