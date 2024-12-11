package techpick.api.domain.user.service.strategy;

import techpick.core.model.user.User;

public interface ContentInitStrategy {
	void initContent(User user, Long folderId);
}