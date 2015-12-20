package org.projectmvc.access;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.projectmvc.access.annotation.AssertParamOrgPrivileges;
import org.projectmvc.access.annotation.AssertWebSystemPrivileges;
import org.projectmvc.entity.BaseEntity;
import org.projectmvc.entity.User;
import org.projectmvc.web.CurrentUserHolder;

import javax.inject.Inject;
import java.lang.reflect.Method;

public class WebAccessInterceptor implements MethodInterceptor {

	@Inject
	AccessManager accessManager;


	@Inject
	CurrentUserHolder currentUserHolder;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();

		//System.out.printf("DaoAccessInterceptor - %s.%s \n", method.getDeclaringClass().getSimpleName(), invocation.getMethod().getName());

		String methodName = method.getName();
		String methodClassName = method.getDeclaringClass().getSimpleName();


		// --------- Assert the AssertWebSystemPrivileges --------- //
		AssertWebSystemPrivileges assertParamSystemPrivileges = method.getAnnotation(AssertWebSystemPrivileges.class);
		if (assertParamSystemPrivileges != null) {
			User user = currentUserHolder.get();

			for (SystemPrivilege privilege : assertParamSystemPrivileges.value()) {
				//System.out.printf("\tAssertParamOrgPrivileges on org %s - privileges: %s \n", orgId, privilege);
				accessManager.assertSystemPrivilege(user.getUserAccessContext(), privilege);
			}
		}
		// --------- /Assert the AssertParamOrgPrivileges --------- //

		Object obj = invocation.proceed();


		return obj;

	}

}
