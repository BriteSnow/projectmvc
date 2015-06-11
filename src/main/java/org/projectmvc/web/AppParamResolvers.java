package org.projectmvc.web;

import com.britesnow.snow.util.AnnotationMap;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.resolver.annotation.WebParamResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.inject.Singleton;
import org.projectmvc.web.annotation.MaybeJson;
import org.projectmvc.web.annotation.JsonParam;

import java.io.IOException;
import java.util.Map;

import static com.britesnow.snow.util.MapUtil.mapIt;

/**
 * Created by jeremychone on 2/8/14.
 */
@Singleton
public class AppParamResolvers {

	ObjectMapper mapper = new ObjectMapper();

	/**
	 * <p>This is a simple json param resolver. I uses the Snow simple JsonUtil but could use
	 * Jackson or any other json processor.</p>
	 *
	 * @return The map for the JSON
	 */
	@WebParamResolver(annotatedWith = JsonParam.class)
	public Map resolveJsonParam(AnnotationMap annotationMap, Class paramType, RequestContext rc) {
		JsonParam jsonParam = annotationMap.get(JsonParam.class);
		String paramName = jsonParam.value();
		String value = rc.getParam(paramName);
		Map r = null;
		if (value != null){
			if (!value.startsWith("{")){
				throw new RuntimeException("Web Param " + paramName + " does not have a valid JSON value (must start with '{'): " + value);
			}
			//r = JsonUtil.toMapAndList(value);
			r = parseJson(value);
		}
		return r;
	}

	/**
	 * If the string start with a '{' it will assume it is a json and return a Map.
	 *
	 * In another other case, just return the raw string value.
	 *
	 *
	 * @param annotationMap
	 * @param paramType
	 * @param rc
	 * @return Return the Map with the name/value from the JSON or the raw value.
	 */
	@WebParamResolver(annotatedWith = MaybeJson.class)
	public Object resolveEntityIdParam(AnnotationMap annotationMap, Class paramType, RequestContext rc) {
		MaybeJson jsonParam = annotationMap.get(MaybeJson.class);
		String paramName = jsonParam.value();
		Object value = rc.getParam(paramName);

		if (value != null){
			if (value instanceof String) {
				String str = (String)value;
				if (str.startsWith("{")) {
					value = parseJson(str);
				}
			}
		}

		return value;
	}


	private Map parseJson(String valueStr){
		Map r = null;
		try {
			r = mapper.readValue(valueStr,Map.class);
		} catch (IOException e) {
			Throwables.propagate(e);
		}
		return r;
	}

}
