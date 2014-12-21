package org.projectmvc.web;

import org.projectmvc.AppException;

import com.google.common.base.Strings;

import java.util.Map;

public class WebResponse {
	
	private Boolean success;
	private Object result;
	private Throwable throwable;
	private Map perf;
	
	
	WebResponse(){
	}

	// --------- Accessors --------- //
	public Boolean getSuccess() {
		return success;
	}
	public WebResponse setSuccess(Boolean success) {
		this.success = success;
		return this;
	}


	public Object getResult() {
		return result;
	}
	public WebResponse setResult(Object result) {
		this.result = result;
		return this;
	}

	public Throwable getThrowable() {
		return throwable;
	}
	public WebResponse setThrowable(Throwable throwable) {
		this.throwable = throwable;
		return this;
	}

	public Map getPerf() {
		return perf;
	}
	void setPerf(Map perf) {
		this.perf = perf;
	}

	public String getErrorCode(){
		if (throwable instanceof AppException){
			return ((AppException) throwable).getErrorCode();
		}else{
			return null;
		}
	}

	public String getErrorMessage() {
		if (throwable instanceof AppException){
			return ((AppException) throwable).getMessage();
		}else{
			return null;
		}
	}
	// --------- /Accessors --------- //


}
