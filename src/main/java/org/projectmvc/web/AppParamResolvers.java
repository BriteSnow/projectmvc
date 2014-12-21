package org.projectmvc.web;

import com.britesnow.snow.util.AnnotationMap;
import com.britesnow.snow.util.JsonUtil;
import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.param.annotation.PathVar;
import com.britesnow.snow.web.param.resolver.annotation.WebParamResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.inject.Singleton;
import org.projectmvc.web.annotation.EntityIdParam;
import org.projectmvc.web.annotation.JsonParam;

import java.io.IOException;
import java.util.HashMap;
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
	 * <p>This is a custom entityId resolver that will get the value from the param and build an
	 * JaSql compatible Id Map. If the value is a direct value (not starting with '{') we will assume
	 * it is a Long and assume the id property name is "id" otherwise, just parse the json and return the map.</p>
	 * <p/>
	 * <p>This simple dual type support  simplifies the REST api by making it simple to get entity with single Primary Key,
	 * while still allowing getting entity that have multiple PKs. So, we can do REST API like
	 * </p>
	 * <code>
	 * HTTP-GET: /get-Project?id=123<br />
	 * HTTP-GET: /get-ProjectUser?id={project_id:123,user_id:24}
	 * </code>
	 *
	 * @param annotationMap
	 * @param paramType
	 * @param rc
	 * @return Return the Map with the name/value from the JSON or the id:Long if direct value. Return null if the value is not compatible.
	 */
	@WebParamResolver(annotatedWith = EntityIdParam.class)
	public Map resolveEntityIdParam(AnnotationMap annotationMap, Class paramType, RequestContext rc) {
		Map idMap = null;
		EntityIdParam jsonParam = annotationMap.get(EntityIdParam.class);
		String paramName = jsonParam.value();
		Object value = rc.getParam(paramName);

		if (value != null) {
			// if it is a json, we parse it.
			if (value instanceof String) {
				String valueStr = (String) value;
				if (valueStr.startsWith("{")) {
					//idMap = JsonUtil.toMapAndList(valueStr);
					idMap = parseJson(valueStr);
				}
			}
			// if idMap is still null, we try to get the value from the param as a direct value.
			// assume "id" as id property name and Long value
			if (idMap == null) {
				Long valueL = rc.getParamAs(paramName,Long.class);
				if (valueL != null){
					idMap = mapIt("id",valueL);
				}
			}
		}

		return idMap;
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
