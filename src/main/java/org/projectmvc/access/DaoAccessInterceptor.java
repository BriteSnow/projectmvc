package org.projectmvc.access;

import com.britesnow.snow.web.CurrentRequestContextHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.projectmvc.AppException;
import org.projectmvc.access.annotation.AssertParamOrgPrivileges;
import org.projectmvc.access.annotation.AssertReturnOrgPrivileges;
import org.projectmvc.access.annotation.AssertReturnProjectPrivileges;
import org.projectmvc.entity.BaseEntity;
import org.projectmvc.entity.OrgScopedEntity;
import org.projectmvc.entity.Project;
import org.projectmvc.entity.User;
import org.projectmvc.perf.PerfManager;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This is the AccessInterceptor for the Dao Classes.
 *
 * - This interceptor should be bound in the AppModule to all Dao methods that need to be guarded.
 *
 * - The responsibility its invoke method is to call the AccessManager access check methods given the annotation
 *   of the method intercepted.
 *
 */
public class DaoAccessInterceptor implements MethodInterceptor {


	@Inject
	AccessManager accessManager;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();

		//System.out.printf("DaoAccessInterceptor - %s.%s \n", method.getDeclaringClass().getSimpleName(), invocation.getMethod().getName());

		String methodName = method.getName();
		String methodClassName = method.getDeclaringClass().getSimpleName();

		// --------- Assert the AssertParamOrgPrivileges --------- //
		AssertParamOrgPrivileges assertParamOrgPrivileges = method.getAnnotation(AssertParamOrgPrivileges.class);
		if (assertParamOrgPrivileges != null) {
			User user = findUser(invocation);
			BaseEntity entity = findParamEntity(invocation);
			UserAccessContext uac = user.getUserAccessContext();

			Long orgId = getOrgIdOrFail(entity, methodClassName, methodName);

			for (OrgPrivilege privilege : assertParamOrgPrivileges.value()) {
				//System.out.printf("\tAssertParamOrgPrivileges on org %s - privileges: %s \n", orgId, privilege);
				accessManager.assertOrgPrivilege(user.getUserAccessContext(), orgId, privilege);
			}
		}
		// --------- /Assert the AssertParamOrgPrivileges --------- //

		Object obj = invocation.proceed();

		// --------- Assert the AssertReturnOrgPrivileges --------- //
		AssertReturnOrgPrivileges assertReturnOrgPrivileges = method.getAnnotation(AssertReturnOrgPrivileges.class);
		if (assertReturnOrgPrivileges != null) {
			User user = findUser(invocation);
			for (BaseEntity entity : normalizeToEntityArray(obj)) {

				Long orgId = getOrgIdOrFail(entity, methodClassName, methodName);

				for (OrgPrivilege privilege : assertReturnOrgPrivileges.value()) {
					//System.out.printf("\tassertReturnOrgPrivileges on org %s - privileges: %s \n", orgId, privilege);
					accessManager.assertOrgPrivilege(user.getUserAccessContext(), orgId, privilege);
				}
			}
		}
		// --------- /Assert the AssertReturnOrgPrivileges --------- //

		// --------- Assert the AssertReturnProjectPrivileges --------- //
		AssertReturnProjectPrivileges assertReturnProjectPrivileges = method.getAnnotation(AssertReturnProjectPrivileges.class);
		if (assertReturnProjectPrivileges != null) {
			User user = findUser(invocation);
			for (BaseEntity entity : normalizeToEntityArray(obj)) {
				if (entity instanceof Project){
					Project project = (Project) entity;
					Long projectId = ((Project) entity).getId();
					for (ProjectPrivilege privilege : assertReturnProjectPrivileges.value()) {
						//System.out.printf("\tassertReturnProjectPrivileges on project %s - privileges: %s \n", project.getId(), privilege);
						accessManager.assertProjectPrivilege(user.getUserAccessContext(), project, privilege);
					}
				}

			}
		}
		// --------- /Assert the AssertReturnProjectPrivileges --------- //

		return obj;
	}

	/**
	 * In the current implementation, the convention is that any guarded dao method must have first first
	 * parameter being the user to be asserted.
	 *
	 * Note: Obviously, this can be changed for different project. The other approach is to use the
	 *       CurrentRequestContextHolder to get the currently authenticated user, however, while this is appropriate
	 *       for WebAccessInterceptor, it is a little bit out of place in the case
	 *
	 */
	private User findUser(MethodInvocation invocation) {
		Object[] args = invocation.getArguments();
		Object obj = args[0];
		if (obj != null && obj instanceof User) {
			return (User)obj;
		} else{
			throw new AppException(AccessError.DAO_METHOD_NO_USER,
					invocation.getMethod().getDeclaringClass().getSimpleName(),
					invocation.getMethod().getName());
		}
	}

	private BaseEntity findParamEntity(MethodInvocation invocation) {
		Object[] args = invocation.getArguments();
		return Arrays.asList(args).stream()
				.skip(1).filter(o -> o instanceof BaseEntity).map(o -> (BaseEntity) o)
				.findFirst().orElseThrow(() -> new AppException(AccessError.DAO_METHOD_NO_ENTITY,
						invocation.getMethod().getDeclaringClass().getSimpleName(),
						invocation.getMethod().getName()));
	}

	/**
	 * Here to keep the calling simpler, we always return an array, even when the return value has only entity or one optional.
	 *
	 * @param returnedObject could be the entity, the optional of an entity, or a list of entities (this is the three returned uspported for now)
	 * @return
	 */
	private <T> BaseEntity[] normalizeToEntityArray(Object returnedObject) {
		List<BaseEntity> entities = new ArrayList<>();

		// if the returned object is null, then, return empty array
		if (returnedObject == null){
			return new BaseEntity[0];
		}
		// if the returned object is of BaseEntity, then, return a single element array with it
		else if (returnedObject instanceof BaseEntity) {
			entities.add((BaseEntity) returnedObject);
		}
		// if the returned object is an Optional and has a BaseEntity, return the single element array
		else if (returnedObject instanceof Optional){
			Optional opt = (Optional)returnedObject;
			if (opt.isPresent() && opt.get() instanceof BaseEntity){
				entities.add((BaseEntity)opt.get());
			}
		}
		// if the returned object is list, then, add the elements if they are base entity
		else if (returnedObject instanceof List){
			List list = (List) returnedObject;
			list.stream().filter(v -> v instanceof BaseEntity).forEach(v -> entities.add((BaseEntity)v));
		}

		return entities.toArray(new BaseEntity[entities.size()]);
	}

	private Long getOrgIdOrFail(BaseEntity entity, String methodClassName, String methodName){
		if (!(entity instanceof OrgScopedEntity)) {
			throw new AppException(AccessError.DAO_METHOD_NO_ORGSCOPEDENTITY, methodClassName,
					methodName, entity);
		}
		Long orgId = ((OrgScopedEntity) entity).getOrgId();
		if (orgId == null){
			throw new AppException(AccessError.DAO_METHOD_ENTITY_ORGID_NULL, methodClassName,
					methodName, entity);
		}
		return orgId;
	}
}


