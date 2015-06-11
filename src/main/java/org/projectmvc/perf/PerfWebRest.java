package org.projectmvc.perf;

import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import org.projectmvc.dao.DaoHelper;
import org.projectmvc.web.WebResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * <p>WebRest methods to get and refresh the Perf info</p>
 */
@Singleton
public class PerfWebRest {


	@Inject
	private PerfManager perfManager;

	@Inject
	DaoHelper daoHelper;

	@WebGet("/perf-get-all")
	public WebResponse getAllPerf(){

		AppPerf appPerf = perfManager.getAppPerf(daoHelper.getPoolInfo());

		return WebResponse.success(appPerf);
	}

	@WebPost("/perf-clear")
	public WebResponse clearPerf(){
		perfManager.clear();
		return WebResponse.success(true);
	}

}
