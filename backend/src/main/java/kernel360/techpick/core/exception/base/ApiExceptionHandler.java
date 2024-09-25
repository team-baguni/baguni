package kernel360.techpick.core.exception.base;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

	/**
	 * ApiException 에서 잡지 못한 예외는
	 * 5xx 코드 오류 입니다.
	 */
	@ExceptionHandler(Exception.class)
	public ApiErrorResponse handleGlobalException(Exception exception) {

		log.error(exception.getMessage(), exception);

		return ApiErrorResponse.UNKNOWN_SERVER_ERROR();
	}

	/**
	 * ApiException 을 공통 Response 형태로 변환 합니다.
	 */
	@ExceptionHandler(ApiException.class)
	public ApiErrorResponse handleApiException(
		HttpServletRequest req,
		HttpServletResponse res,
		ApiException exception
	) {
		log.error(exception.getMessage(), exception);

		exception.handleError(req, res);

		return ApiErrorResponse.of(exception.getApiErrorCode());
	}
}
