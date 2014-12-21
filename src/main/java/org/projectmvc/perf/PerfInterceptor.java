package org.projectmvc.perf;

import com.britesnow.snow.web.CurrentRequestContextHolder;
import com.britesnow.snow.web.RequestContext;
import com.google.inject.Singleton;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.inject.Inject;

import static org.projectmvc.perf.PerfManager.PerfContext;

/**
 * <p></p>
 */
@Singleton
public class PerfInterceptor implements MethodInterceptor {

	@Inject
	PerfManager perfManager;

	@Inject
	CurrentRequestContextHolder crch;

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		Object result;

		String name = methodInvocation.getMethod().getDeclaringClass().getSimpleName() + "." + methodInvocation.getMethod().getName();

		RequestContext rc = (crch != null)?crch.getCurrentRequestContext():null;
		RcPerf rcPerf = (rc != null)?rc.getData(RcPerf.class):null;

		PerfContext requestPerfContext = (rcPerf != null)?rcPerf.start(name):null;
		PerfContext methodPerfContext = (perfManager != null)?perfManager.startMethod(name):null;
		try{
			result = methodInvocation.proceed();
		}finally{
			if (methodPerfContext != null) methodPerfContext.end();
			if (requestPerfContext != null) requestPerfContext.end();
		}
		return result;


	}
}
