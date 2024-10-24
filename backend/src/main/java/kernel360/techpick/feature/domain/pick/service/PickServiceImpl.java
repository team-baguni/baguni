package kernel360.techpick.feature.domain.pick.service;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import kernel360.techpick.core.model.folder.Folder;
import kernel360.techpick.core.model.pick.Pick;
import kernel360.techpick.feature.domain.pick.dto.PickCommand;
import kernel360.techpick.feature.domain.pick.dto.PickMapper;
import kernel360.techpick.feature.domain.pick.dto.PickResult;
import kernel360.techpick.feature.infrastructure.folder.FolderAdapter;
import kernel360.techpick.feature.infrastructure.link.LinkAdaptor;
import kernel360.techpick.feature.infrastructure.pick.PickAdaptor;
import kernel360.techpick.feature.infrastructure.user.UserAdaptor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PickServiceImpl implements PickService {
	private final UserAdaptor userAdaptor;
	private final LinkAdaptor linkAdaptor;
	private final PickAdaptor pickAdaptor;
	private final PickMapper pickMapper;
	private final FolderAdapter folderAdapter;

	@Override
	@Transactional(readOnly = true)
	public PickResult getPick(PickCommand.Read command) {
		var user = userAdaptor.getUser(command.userId());
		var pick = pickAdaptor.getPick(user, command.pickId());
		return pickMapper.toReadResult(pick);
	}

	@Override
	@Transactional
	public PickResult saveNewPick(PickCommand.Create command) {
		var user = userAdaptor.getUser(command.userId());
		var folder = folderAdapter.readFolder(user, command.parentFolderId());
		var link = linkAdaptor.saveLink(command.linkInfo());
		var pick = pickAdaptor.savePick(pickMapper.toEntity(command, user, folder, link));
		return pickMapper.toCreateResult(pick);
	}

	@Override
	@Transactional
	public PickResult updatePick(PickCommand.Update command) {
		var user = userAdaptor.getUser(command.userId());
		var pick = pickAdaptor.getPick(user, command.pickId())
							  .updateTitle(command.title()).updateMemo(command.memo());
		updateNewTagIdList(pick, command.tagIdList());
		return pickMapper.toUpdateResult(pick);
	}

	@Override
	@Transactional
	public PickResult movePick(PickCommand.Move command) {
		var user = userAdaptor.getUser(command.userId());
		var pick = pickAdaptor.getPick(user, command.pickId());
		var destinationFolder = folderAdapter.readFolder(user, command.parentFolderId());

		if (isParentFolderChanged(pick.getParentFolder(), destinationFolder)) {
			movePickToOtherFolder(pick, destinationFolder, command.orderIdx());
			return pickMapper.toMoveResult(pick);
		}
		pick.getParentFolder().updateChildPickOrder(command.pickId(), command.orderIdx());
		return pickMapper.toMoveResult(pick);
	}

	@Override
	@Transactional
	public void deletePick(PickCommand.Delete command) {
		var user = userAdaptor.getUser(command.userId());
		var pick = pickAdaptor.getPick(user, command.pickId());
		pick.getParentFolder().removeChildPickOrder(command.pickId());
		pickAdaptor.deletePick(pick);
	}

	/**
	 * Internal Helper Functions
	 **/
	private boolean isParentFolderChanged(Folder originalFolder, Folder destinationFolder) {
		return ObjectUtils.notEqual(originalFolder, destinationFolder);
	}

	private void movePickToOtherFolder(Pick pick, Folder destinationFolder, int orderIndex) {
		pick.getParentFolder().removeChildPickOrder(pick.getId());
		destinationFolder.updateChildPickOrder(pick.getId(), orderIndex);
		pick.updateParentFolder(destinationFolder);
	}

	private void updateNewTagIdList(Pick pick, List<Long> newTagOrderList) {
		pick.getTagOrder().stream()
			.filter(tagId -> !newTagOrderList.contains(tagId))
			.forEach(tagId -> pickAdaptor.detachTagFromPick(pick, tagId));
		pick.updateTagOrderList(newTagOrderList);
	}
}
