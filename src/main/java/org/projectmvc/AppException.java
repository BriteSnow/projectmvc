package org.projectmvc;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AppException extends RuntimeException {

	private Enum errorEnum;

	private Object[] vals;

	public AppException(Enum errorEnum){
		this.errorEnum = errorEnum;
	}

	public AppException(Enum errorEnum, Object... vals){
		this.errorEnum = errorEnum;
		this.vals = vals;
	}


	public AppException(String message) {
		super(message);
	}
	
	public Enum getErrorEnum(){
		return this.errorEnum;
	}
	
	public String getErrorCode(){
		if (errorEnum != null){
			return errorEnum.name();
		}else{
			return null;
		}
	}

	public String getMessage(){
		if (errorEnum != null){
			if (errorEnum instanceof ErrorType){
				ErrorType errorType = (ErrorType) errorEnum;
				StringBuilder sb = new StringBuilder(errorEnum.name()).append(": ");
				if (vals != null && vals.length > 0){
					try {
						sb.append(errorType.formatMessage(vals));
					}catch (Throwable t){
						sb.append(" (can't format, illegal format parameters) - raw message and values:\n");
						sb.append("\n\tmessage: " + errorType.getMessage());
						sb.append("\n\tvalues: " + Arrays.asList(vals).stream().map(Object::toString).collect(Collectors.joining(", ")));
					}
				}else {
					sb.append(errorType.getMessage());
				}
				return sb.toString();
			}else{
				return getErrorCode();
			}
		}else{
			return super.getMessage();
		}
	}
}
