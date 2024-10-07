package kernel360.techpick.feature.folder.service.dto;

import org.springframework.security.core.Authentication;

import lombok.Getter;

@Getter
public class FolderUpdateRequest {

	private Long userId;
	private Long folderId;
	private String name;

	public void updateUserIdAndFolderId(Authentication auth, Long folderId) {
		this.userId = (Long)auth.getPrincipal();
		this.folderId = folderId;
	}
}
