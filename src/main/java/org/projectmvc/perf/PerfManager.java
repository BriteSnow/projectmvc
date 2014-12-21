package org.projectmvc.perf;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import javax.inject.Singleton;
import java.util.Map;

/**
 * <p>Preformance Manager </p>
 */
@Singleton
public class PerfManager {

	private volatile MetricRegistry methodMetrics = new MetricRegistry();
	private volatile MetricRegistry requestMetrics = new MetricRegistry();


	RcPerf newRcPerf(String pathInfo){
		return new RcPerf(pathInfo, requestMetrics);
	}

	public void clear(){
		methodMetrics = new MetricRegistry();
		requestMetrics = new MetricRegistry();
	}

	public PerfContext startMethod(String name){
		return start(methodMetrics,name);
	}

	public AppPerf getAppPerf(Map poolInfo){
		return new AppPerf(poolInfo, methodMetrics,requestMetrics);
	}


	private PerfContext start(MetricRegistry metrics, String name){
		Timer timer = metrics.timer(name);
		final Timer.Context ctx = timer.time();

		return () -> ctx.stop();
	}

	public static interface PerfContext{
		public void end();
	}


}
