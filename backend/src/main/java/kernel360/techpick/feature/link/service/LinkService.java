package kernel360.techpick.feature.link.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kernel360.techpick.core.exception.feature.link.ApiLinkException;
import kernel360.techpick.core.model.link.Link;
import kernel360.techpick.feature.link.model.dto.LinkRequest;
import kernel360.techpick.feature.link.model.dto.LinkResponse;
import kernel360.techpick.feature.link.repository.LinkRepository;
import kernel360.techpick.feature.pick.repository.PickRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LinkService {

	private final LinkRepository linkRepository;
	private final PickRepository pickRepository;

	/**
	 * 주어진 URL이 데이터베이스에 존재하지 않는 경우 새로운 {@code Link}를 생성
	 *
	 * @param request {@code LinkRequest}
	 * @throws ApiLinkException [LI-002] 이미 존재하는 링크(URL)
	 */
	public void create(LinkRequest request) throws ApiLinkException {

		Link link = Link.create(request.url(), request.title(), request.description());
		if (existsByUrl(link.getUrl())) {
			throw ApiLinkException.LINK_ALREADY_EXISTS();
		}
		linkRepository.save(link);
	}

	public boolean existsByUrl(String url) {

		return linkRepository.existsByUrl(url);
	}

	public LinkResponse getById(Long id) throws ApiLinkException {

		Link link = linkRepository.findById(id).orElseThrow(ApiLinkException::LINK_NOT_FOUND);

		return LinkResponse.of(link);
	}

	public LinkResponse getByUrl(String url) throws ApiLinkException {

		Link link = linkRepository.findByUrl(url).orElseThrow(ApiLinkException::LINK_NOT_FOUND);

		return LinkResponse.of(link);
	}

	public List<LinkResponse> getAll() {

		List<Link> linkList = linkRepository.findAll();

		return linkList.stream()
			.map(LinkResponse::of)
			.toList();
	}

	public void deleteById(Long id) throws ApiLinkException {

		Link targetLink = linkRepository.findById(id).orElseThrow(ApiLinkException::LINK_NOT_FOUND);
		if (pickRepository.existsByLink(targetLink)) {
			throw ApiLinkException.LINK_HAS_PICKS();
		}
		linkRepository.deleteById(id);
	}
}
