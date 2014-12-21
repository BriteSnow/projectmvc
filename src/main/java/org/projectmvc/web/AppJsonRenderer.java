package org.projectmvc.web;

import com.britesnow.snow.web.CurrentRequestContextHolder;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.renderer.JsonLibJsonRenderer;
import com.britesnow.snow.web.renderer.JsonRenderer;
import org.projectmvc.perf.RcPerf;
import org.projectmvc.web.WebResponse;

import javax.inject.Inject;
import java.io.Writer;

/**
 * A simple implementation of JsonRenderer using the default default JsonLibRenderer but also endRequest on the RcPref
 * and set the rcPerf to the WebResponse.
 */
public class AppJsonRenderer implements JsonRenderer{

	@Inject
	private CurrentRequestContextHolder crch;

	@Inject
	private JsonLibJsonRenderer jsonLibRenderer;

	@Override
	public void render(Object data, Writer out) {
		RequestContext rc = crch.getCurrentRequestContext();

		if (rc != null && data instanceof WebResponse) {
			WebResponse webResponse = (WebResponse) data;
			RcPerf rcPerf = rc.getData(RcPerf.class);
			rcPerf.endRequest();
			webResponse.setPerf(rcPerf.getRcPerfInfo());
		}

		jsonLibRenderer.render(data,out);
	}
}
