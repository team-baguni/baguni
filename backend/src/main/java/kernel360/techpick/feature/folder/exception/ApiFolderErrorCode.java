package kernel360.techpick.feature.folder.exception;

import org.springframework.http.HttpStatus;

import kernel360.techpick.core.exception.base.ApiErrorCode;
import kernel360.techpick.core.exception.level.ErrorLevel;

public enum ApiFolderErrorCode implements ApiErrorCode {

	/**
	 * Folder Error Code (FO)
	 */
	FOLDER_NOT_FOUND("FO-000", HttpStatus.BAD_REQUEST, "존재하지 않는 폴더", ErrorLevel.SHOULD_NOT_HAPPEN()),
	FOLDER_ALREADY_EXIST("FO-001", HttpStatus.BAD_REQUEST, "이미 존재하는 폴더 이름", ErrorLevel.CAN_HAPPEN()),
	FOLDER_ACCESS_DENIED("FO-002", HttpStatus.UNAUTHORIZED, "접근할 수 없는 폴더", ErrorLevel.SHOULD_NOT_HAPPEN()),

	;

	private final String code;

	private final HttpStatus httpStatus;

	private final String errorMessage;

	private final ErrorLevel errorLevel;

	ApiFolderErrorCode(String code, HttpStatus status, String message, ErrorLevel errorLevel) {
		this.code = code;
		this.httpStatus = status;
		this.errorMessage = message;
		this.errorLevel = errorLevel;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.errorMessage;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}

	@Override
	public ErrorLevel getErrorLevel() {
		return this.errorLevel;
	}
}