package org.projectmvc.web;

import com.britesnow.snow.web.handler.annotation.WebModelHandler;
import com.britesnow.snow.web.param.annotation.WebModel;
import com.google.inject.Singleton;
import org.projectmvc.AppModule;

import java.util.Map;

@Singleton
public class CommonWebHandlers {

	@WebModelHandler(startsWith="/")
	public void allWebPages(@WebModel Map model){
		model.put("appVersion", AppModule.appVersion);
	}
}
