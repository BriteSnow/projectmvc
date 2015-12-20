package org.projectmvc.perf;

import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import org.jomni.JomniBuilder;
import org.jomni.JomniMapper;
import org.projectmvc.access.SystemPrivilege;
import org.projectmvc.access.annotation.AssertWebSystemPrivileges;
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

	@AssertWebSystemPrivileges(SystemPrivilege.PERF)
	@WebGet("/perf-get-all")
	public WebResponse getAllPerf(){

		AppPerf appPerf = perfManager.getAppPerf(daoHelper.getPoolInfo());

		return WebResponse.success(appPerf);
	}

	@AssertWebSystemPrivileges(SystemPrivilege.PERF)
	@WebPost("/perf-clear")
	public WebResponse clearPerf(){
		perfManager.clear();
		return WebResponse.success(true);
	}

}
