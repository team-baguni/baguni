package kernel360.techpick.feature.tag.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kernel360.techpick.core.exception.feature.tag.ApiTagException;
import kernel360.techpick.core.exception.feature.user.ApiUserException;
import kernel360.techpick.core.model.tag.Tag;
import kernel360.techpick.feature.pick.repository.PickTagRepository;
import kernel360.techpick.feature.tag.model.TagMapper;
import kernel360.techpick.feature.tag.model.TagProvider;
import kernel360.techpick.feature.tag.model.TagUpdater;
import kernel360.techpick.feature.tag.model.dto.TagCreateRequest;
import kernel360.techpick.feature.tag.model.dto.TagResponse;
import kernel360.techpick.feature.tag.model.dto.TagUpdateRequest;
import kernel360.techpick.feature.tag.validator.TagValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagService {

	private final TagMapper tagMapper;
	private final TagProvider tagProvider;
	private final TagValidator tagValidator;
	private final PickTagRepository pickTagRepository;

	@Transactional
	public TagResponse createTag(Long userId, TagCreateRequest request) throws ApiTagException, ApiUserException {

		tagValidator.validateTagNameExists(userId, request.name());

		int lastOrder = tagProvider.getLastOrderByUserId(userId);
		Tag tag = tagProvider.save(tagMapper.createTag(request, lastOrder, userId));

		return tagMapper.createTagResponse(tag);
	}

	@Transactional(readOnly = true)
	public List<TagResponse> getTagListByUser(Long userId) {

		List<Tag> tagList = tagProvider.findAllByUserIdOrderByTagOrder(userId);

		return tagList.stream()
			.map(tagMapper::createTagResponse)
			.toList();
	}

	@Transactional
	public List<TagResponse> updateTagList(Long userId, List<TagUpdateRequest> tagUpdateRequests) throws
		ApiTagException {

		TagUpdater tagUpdater = tagProvider.getUserTag(userId);

		for (var req : tagUpdateRequests) {
			tagUpdater.updateTag(req);
		}
		tagUpdater.validateTagOrder();

		return tagProvider.saveAll(tagUpdater.getTags())
			.stream()
			.map(tagMapper::createTagResponse)
			.toList();
	}

	@Transactional
	public void deleteById(Long userId, Long tagId) throws ApiTagException {

		Tag targetTag = tagProvider.findById(tagId);
		tagValidator.validateTagAccess(userId, targetTag);
		// 해당 태그를 등록한 픽에서 해당 태그를 모두 삭제
		// TODO: PickTagProvider 구현되면 PickTagProvider를 의존하도록 리팩토링 필요
		pickTagRepository.deleteByTag_Id(tagId);
		tagProvider.deleteById(tagId);
	}

}
