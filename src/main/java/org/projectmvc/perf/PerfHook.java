package org.projectmvc.perf;


import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.WebRequestType;
import com.britesnow.snow.web.hook.On;
import com.britesnow.snow.web.hook.ReqPhase;
import com.britesnow.snow.web.hook.annotation.WebRequestHook;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.britesnow.snow.web.hook.ReqPhase.END;
import static com.britesnow.snow.web.hook.ReqPhase.START;

/**
 *
 *
 */
@Singleton
public class PerfHook {



	@Inject
	private PerfManager perfManager;

	@WebRequestHook(phase = START)
	public void startReqPerf(RequestContext rc) {
		// for now, monitor only json/webrest http request.
		// Note that the "rcPerf.endRequest" is in the jsonRender, so, with this scheme
		// only JSON webrest call are timed.
		if (rc.getWebRequestType() == WebRequestType.WEB_REST){
			RcPerf rcPerf = perfManager.newRcPerf(rc.getPathInfo());
			rc.setData(rcPerf);
			rcPerf.startRequest();
		}

	}



}
