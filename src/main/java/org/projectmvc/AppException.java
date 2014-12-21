package org.projectmvc;

public class AppException extends RuntimeException {

	private Enum errorEnum;

	public AppException(Enum errorEnum){

		this.errorEnum = errorEnum;
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
			if (errorEnum instanceof ErrorEnum){
				return ((ErrorEnum)errorEnum).getMessage();
			}else{
				return getErrorCode();
			}
		}else{
			return super.getMessage();
		}
	}
}
