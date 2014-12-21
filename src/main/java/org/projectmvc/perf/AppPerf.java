package org.projectmvc.perf;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import org.apache.commons.collections.map.HashedMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>Object to be returned when an Application Perf is requested. It takes the snapshots of all the timer for methods and request metrics.</p>
 */
public class AppPerf {

	private final Map<String, Snap> methodsPerf;
	private final Map<String, Snap> requestsPerf;
	private final Map poolInfo;

	AppPerf(Map poolInfo, MetricRegistry methodMetrics, MetricRegistry requestMetrics){
		this.poolInfo = poolInfo;
		methodsPerf = extractSnapshots(methodMetrics);
		requestsPerf =  extractSnapshots(requestMetrics);
	}

	public Map getJavaInfo() {
		final long mbRatio = 1024 * 1024;
		Map<String, Long> javaInfo = new HashMap<String, Long>();
		Runtime runtime = Runtime.getRuntime();

		long maxMemory = runtime.maxMemory();
		javaInfo.put("maxMemory",maxMemory / mbRatio);
		long allocatedMemory = runtime.totalMemory();
		javaInfo.put("allocatedMemory",allocatedMemory / mbRatio);
		long freeMemory = runtime.freeMemory();
		javaInfo.put("freeMemory",freeMemory / mbRatio);

		return javaInfo;
	}

	public Map getPoolInfo(){
		return poolInfo;
	}

	/**
	 * Return all the perf snapshot per by methods.
	 *
	 */
	public Map<String,Snap> getMethodsPerf(){
		return methodsPerf;
	}

	/**
	 * Return the Snap per name of all the WebRest request
	 * @return
	 */
	public Map<String,Snap> getRequestsPerf(){
		return requestsPerf;
	}

	private Map<String,Snap> extractSnapshots(MetricRegistry metrics) {
		Map<String, Snap> r = new HashMap<String, Snap>();

		Map<String, Timer> methodTimers = metrics.getTimers();
		for (String name : methodTimers.keySet()) {
			Timer timer = methodTimers.get(name);
			timer.getCount();
			Snapshot snapshot = timer.getSnapshot();
			r.put(name,new Snap(timer.getCount(),snapshot));
		}
		return r;
	}

	/**
	 * Just a little Metrics.Snapshot wrapper.
	 * Used to avoid getValues, return in milliseconds and change soeme property names.
	 */
	public static class Snap{
		private final Snapshot snapshot;
		private final long count;

		Snap(long count, Snapshot snapshot){
			this.count = count;
			this.snapshot = snapshot;
		}

		public Long getCount(){
			return count;
		}

		public Long getMin(){
			return TimeUnit.MILLISECONDS.convert(snapshot.getMin(), TimeUnit.NANOSECONDS);
		}

		public Long getMean(){
			Double med = snapshot.getMean();
			return TimeUnit.MILLISECONDS.convert(med.longValue(), TimeUnit.NANOSECONDS);
		}

		public Long getMax(){
			return TimeUnit.MILLISECONDS.convert(snapshot.getMax(), TimeUnit.NANOSECONDS);
		}
	}
}
