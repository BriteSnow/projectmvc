package org.projectmvc.web;

import com.britesnow.snow.web.param.annotation.PathVar;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.j8ql.query.Condition;
import org.j8ql.query.FieldOpValue;
import org.j8ql.query.Query;
import org.jomni.JomniMapper;
import org.projectmvc.AppException;
import org.projectmvc.access.AccessManager;
import org.projectmvc.dao.DaoRegistry;
import org.projectmvc.dao.IDao;
import org.projectmvc.entity.User;
import org.projectmvc.perf.annotation.ToMonitor;
import org.projectmvc.web.annotation.MaybeJson;
import org.projectmvc.web.annotation.JsonParam;

import java.util.List;
import java.util.Map;

/**
 * <p>This is the generic Data Access Service layer from the server.</p>
 *
 * <p><strong>Note:</strong> Non parameterized @WebRest (i.e. @WebGet("/das-get-Project") will take precedence over
 * parameterized ones (i.e. @WebGet("das-get-{entity}"). Therefore, this GenericDasWebRest can been seen as a fall back
 * fall back when no specialized DasWebRest methods are handling specific entity.</p>
 *
 * <p><strong>Best Practice:</strong> Often, when starting a new project, this is a great and and simple way to start connecting
 * your client to those Web[REST] APIs. As the application becomes more sophisticated, you can override those generic bindings with
 * more specifics ones (e.g., for added security and business rules) without ever changing the UI code.</p>
 *
 */
@Singleton
@ToMonitor
public class GenericDasWebRest {

	@Inject
	private DaoRegistry daoRegistry;

	@Inject
	private AccessManager accessManager;

	@Inject
	private JomniMapper jomni;

	@WebPost("/das-create-{entity}")
	public WebResponse createEntity(@WebUser User user, @PathVar("entity")String entityType, @JsonParam("props") Map props){
		IDao dao = daoRegistry.getDao(entityType);

		// TODO: probably need to have a createWithReturn
		Object id = dao.create(user,props);
		Object entity = dao.get(user, id).orElse(null);
		return WebResponse.success(entity);
	}

	@WebGet("/das-get-{entity}")
	public WebResponse getEntity(@WebUser User user, @PathVar("entity")String entityType, @MaybeJson("id") Object idObj){
		IDao dao = daoRegistry.getDao(entityType);
		Object id = getIdValue(dao.getIdClass(),idObj);

		Object entity = dao.get(user, id).orElse(null);

		return WebResponse.success(entity);
	}

	@WebGet("/das-list-{entity}")
	public WebResponse listEntity(@WebUser User user, @PathVar("entity")String entityType, @JsonParam("filter") Map filter){
		IDao dao = daoRegistry.getDao(entityType);

		Condition conditions = null;
		if (filter != null) {
			// build the Field Operation and Value from the map, assuming it is "J8QL" map compatible (e.g. "projectId;=":123)
			FieldOpValue[] fovs = Query.fovs(filter);
			conditions = Query.and(fovs);
		}
		List<Object> list = dao.list(user,conditions,0,1000);

		return WebResponse.success(list);
	}



	@WebPost("/das-update-{entity}")
	public WebResponse updateEntity(@WebUser User user, @PathVar("entity")String entityType, @MaybeJson("id") Object idObj, @JsonParam("props") Map props){
		IDao dao = daoRegistry.getDao(entityType);
		Object id = getIdValue(dao.getIdClass(),idObj);

		int r = dao.update(user,props, id);
		return WebResponse.success(r);
	}


	@WebPost("/das-delete-{entity}")
	public WebResponse deleteEntity(@WebUser User user, @PathVar("entity")String entityType, @MaybeJson("id") Object idObj){
		IDao dao = daoRegistry.getDao(entityType);
		Object id = getIdValue(dao.getIdClass(),idObj);

		int numDeleted = dao.delete(user, id);

		if (numDeleted > 0){
			return WebResponse.success(id);
		}else{
			return WebResponse.fail(new AppException("Cannot delete " + entityType + " with id " + id));
		}
	}


	private <I> I getIdValue(Class<I> idClass, Object idObj) {
		return jomni.as(idClass, idObj);
	}

}
