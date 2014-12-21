package org.projectmvc.test;


import com.britesnow.snow.testsupport.mock.RequestContextMock;
import com.britesnow.snow.util.JsonUtil;
import com.googlecode.gentyref.GenericTypeReflector;
import org.junit.Test;
import org.projectmvc.dao.*;
import org.projectmvc.entity.*;

import java.lang.reflect.Type;
import java.util.*;

import static com.britesnow.snow.util.MapUtil.mapIt;
import static org.junit.Assert.assertEquals;

/**
 * Created by jeremychone on 1/25/14.
 */
public class DevTest extends BaseTestSupport {

	@Test
	public void emptyTest(){
	}


	//@Test
	public void getIdClassesFromEntity(){
		Class[] entityClasses = {Project.class,Team.class,TeamUser.class};
		for (Class entityClass : entityClasses ){
			Type idType = GenericTypeReflector.getTypeParameter(entityClass, BaseEntity.class.getTypeParameters()[0]);
			System.out.println(idType);
		}
	}

	//@Test
	public void webTest(){
		DaoRegistry daoRegistry = appInjector.getInstance(DaoRegistry.class);
		Map responseMap;
		List<Map> list;

		IDao<Project,Long> projectDao = daoRegistry.getDao(Project.class);
		UserDao userDao = appInjector.getInstance(UserDao.class);

		User user1 = userDao.createUser("test_demouser_1","welcome");

		Map user1CookieMap = doPost("/login", mapIt("username", user1.getUsername(), "pwd", user1.getPwd())).getCookieMap();

		String projectJsonStr;
		RequestContextMock rc;

		projectJsonStr = JsonUtil.toJson(mapIt("name", "test_project_1_user1"));
		rc = doPost("/das-create-project",mapIt("props",projectJsonStr), user1CookieMap);
		projectJsonStr = JsonUtil.toJson(mapIt("name", "test_project_2_user1"));
		rc = doPost("/das-create-project",mapIt("props",projectJsonStr), user1CookieMap);

		rc = doGet("/das-list-project", null, user1CookieMap);
		projectJsonStr = rc.getResponseAsString();
		System.out.println(projectJsonStr);
	}


}
