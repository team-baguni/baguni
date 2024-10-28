package kernel360.techpick.feature.api.tag.dto;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import kernel360.techpick.feature.domain.tag.dto.TagCommand;
import kernel360.techpick.feature.domain.tag.dto.TagResult;

@Mapper(
	componentModel = "spring",
	injectionStrategy = InjectionStrategy.CONSTRUCTOR,
	unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TagApiMapper {

	TagCommand.Read toReadCommand(Long userId, TagApiRequest.Read request);

	TagCommand.Create toCreateCommand(Long userId, TagApiRequest.Create request);

	TagCommand.Update toUpdateCommand(Long userId, TagApiRequest.Update request);

	TagCommand.Move toMoveCommand(Long userId, TagApiRequest.Move request);

	TagCommand.Delete toDeleteCommand(Long userId, TagApiRequest.Delete request);

	TagApiResponse.Read toReadResponse(TagResult result);

	TagApiResponse.Create toCreateResponse(TagResult result);

	TagApiResponse.Update toUpdateResponse(TagResult result);
}
