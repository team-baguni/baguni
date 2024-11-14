package techpick.api.application.pick.dto;

import java.util.List;

import org.springframework.data.domain.Slice;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class SliceResponse<T> {
	private final List<T> content;
	private final Long lastCursor;
	private final int size;

	public SliceResponse(Slice<T> slice) {
		this.content = slice.getContent();
		this.lastCursor =
			slice.hasContent() ? ((PickApiResponse.Pick)slice.getContent().get(slice.getNumberOfElements() - 1)).id() :
				null;
		this.size = slice.getSize();
	}
}