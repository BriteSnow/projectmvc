package org.projectmvc.perf;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.slf4j.Logger;

import java.util.*;

import static com.codahale.metrics.MetricRegistry.name;
import static com.codahale.metrics.Timer.Context;
import static org.projectmvc.perf.PerfManager.PerfContext;

/**
 * <p>Request Context Performance object for a given Request (i.e. RequestContext). This is created at the beginning of every request and hold the duration for each timer.</p>
 *
 * <p>So, it keeps a very simplistic name/duration map for the request, and also use the PerformanceManager to track the time across
 * request (which uses Metrics).</p>
 *
 * <p>Single Thread: This is supposed to be used in conjunction of the RequestContext, and therefore is assumed to be used in
 * a single thread.</p>
 */
public class RcPerf {
	static private final Logger logger = org.slf4j.LoggerFactory.getLogger(RcPerf.class);

	// this is for the application wide request metrics (using Metrics).
	private final MetricRegistry requestsMetrics;
	private final String pathInfo;
	private Context requestMetricsStartContext;

	// single request metrics (not using Metrics)
	Long requestOffset = null;
	Map<String,RcTimer> rootRcTimers = new HashMap<String, RcTimer>();
	final Queue<RcTimer> rcTimerStack = Collections.asLifoQueue(new ArrayDeque<RcTimer>());

	public PerfContext rootRequestPerfCtx;

	public RcPerf(String pathInfo, MetricRegistry requestsMetrics) {
		this.pathInfo = pathInfo;
		this.requestsMetrics = requestsMetrics;
	}

	public void startRequest() {
		rootRequestPerfCtx = start("req");
		Timer metricsTimer = requestsMetrics.timer(pathInfo);
		requestMetricsStartContext = metricsTimer.time();
	}

	public void endRequest() {
		if (rootRequestPerfCtx != null) {
			rootRequestPerfCtx.end();
		}
		if (requestMetricsStartContext != null){
			requestMetricsStartContext.stop();
		}
	}

	public PerfContext start(String name){
		RcTimer rcTimer;
		Map<String,RcTimer> rcTimerMap = getRcTimerMap();

		// We check if we have an already existing RcTimer for this name in the rcTimerMap
		rcTimer = rcTimerMap.get(name);

		// if we do not have, we create a new one
		if (rcTimer == null) {
			rcTimer = newRcTimer(name);
			rcTimerMap.put(name, rcTimer);
		}

		rcTimerStack.add(rcTimer);

		rcTimer.start();

		final RcTimer finalRcTimer = rcTimer;

		return () -> finalRcTimer.end();
	}

	/**
	 * Get the RcTimerMap (name/rcTimer) from the current RcTimer in the stack or from the rootRcTimers;
	 *
	 * @return
	 */
	private Map<String,RcTimer> getRcTimerMap(){
		Map<String,RcTimer> rcTimerMap;
		// get the current rcTimerMap for this level
		// First, look in the stack if we are already running a timer
		RcTimer currentRcTimer = rcTimerStack.peek();
		if (currentRcTimer != null){
			// if we have a RcTimer in the stack, we take it subs as the map
			rcTimerMap = currentRcTimer.getSubs();
		}else{
			// otherwise, we take the rootRcTimers
			rcTimerMap = rootRcTimers;
		}
		return rcTimerMap;
	}


	private RcTimer newRcTimer(String name){
		RcTimer rcTimer;
		Long timerStart;
		if (requestOffset == null){
			requestOffset = System.currentTimeMillis();
			timerStart = requestOffset;
		}else{
			timerStart = System.currentTimeMillis();
		}
		return new RcTimer(name,timerStart);
	}

	public Map getRcPerfInfo(){
		return rootRcTimers;
	}


	public class RcTimer{
		private String name;

		private int count = 0;

		private Long firstTimerStart = null;
		private Long timerStart = null;
		private Long timerEnd = null;

		private Long duration = 0L;

		private Map<String,RcTimer> subs = new HashMap<String,RcTimer>();

		private RcTimer(String name, Long timerStart) {
			this.name = name;
			this.timerStart = timerStart;
			this.firstTimerStart = timerStart;
		}

		RcTimer start(){
			count++;
			timerStart = (timerStart != null)?timerStart:System.currentTimeMillis();
			return this;
		}

		RcTimer end(){
			timerEnd = System.currentTimeMillis();
			duration += timerEnd - timerStart;
			timerStart = null;
			RcTimer fromStack = rcTimerStack.peek();
			if (fromStack == this){
				rcTimerStack.poll();
			}else{
				logger.warn("RcTimer.end does not match with the latest one in the stack.\n" +
				"\tfrom rcTimer: " + this + "\n" +
				"\tfrom stack: " + fromStack + "\n");
			}
			return this;
		}


		public Integer getCount(){
			return count;
		}

		public Map<String,RcTimer> getSubs() {
			return subs;
		}

		public RcTimer addSub(String name, RcTimer rcTimer) {
			subs.put(name, rcTimer);
			return this;
		}

		public RcTimer getSub(String name) {
			return subs.get(name);
		}

		/**
		 * Return the relative start from the requestOffset
		 * @return
		 */
		public Long getStart(){
			return firstTimerStart - requestOffset;
		}

		public Long getDuration(){
			return duration;
		}

		public String toString(){return "RcPref[" + name + "]";}
	}
}
