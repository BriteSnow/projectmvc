package org.projectmvc.test.web;

import com.britesnow.snow.testsupport.mock.RequestContextMock;
import com.britesnow.snow.util.JsonUtil;
import org.junit.Test;
import org.projectmvc.dao.DaoHelper;
import org.projectmvc.dao.DaoRegistry;
import org.projectmvc.dao.IDao;
import org.projectmvc.dao.UserDao;
import org.projectmvc.entity.Project;
import org.projectmvc.entity.User;
import org.projectmvc.test.BaseTestSupport;

import java.util.List;
import java.util.Map;

import static com.britesnow.snow.util.MapUtil.mapIt;
import static org.junit.Assert.assertEquals;

public class ProjectWebTest extends BaseTestSupport {

	@Test
	public void projectAndTeam(){
		DaoRegistry daoRegistry = appInjector.getInstance(DaoRegistry.class);
		Map responseMap;
		List<Map> list;

		IDao<Project,Long> projectDao = daoRegistry.getDao(Project.class);
		UserDao userDao = appInjector.getInstance(UserDao.class);

		User user1 = userDao.createUser("test_demouser_1","welcome",123L);
		User user2 = userDao.createUser("test_demouser_2","welcome",123L);

		Map user1CookieMap = doPost("/login", mapIt("username", user1.getUsername(), "pwd", user1.getPwd())).getCookieMap();
		Map user2CookieMap = doPost("/login", mapIt("username", user2.getUsername(), "pwd", user1.getPwd())).getCookieMap();

		String projectJsonStr;
		RequestContextMock rc;

		projectJsonStr = JsonUtil.toJson(mapIt("name", "test_project_1_user1","orgId","123"));
		rc = doPost("/das-create-project",mapIt("props",projectJsonStr), user1CookieMap);
		projectJsonStr = JsonUtil.toJson(mapIt("name", "test_project_2_user1","orgId","123"));
		rc = doPost("/das-create-project",mapIt("props",projectJsonStr), user1CookieMap);

		projectJsonStr = JsonUtil.toJson(mapIt("name", "test_project_1_user2","orgId","123"));
		rc = doPost("/das-create-project",mapIt("props",projectJsonStr), user2CookieMap);

		rc = doGet("/das-list-project", null, user1CookieMap);
		list = (List<Map>) rc.getResponseAsJson().get("result");
		assertEquals(2,list.size());

		rc = doGet("/das-list-project",null, user2CookieMap);
		list = (List<Map>) rc.getResponseAsJson().get("result");
		assertEquals(1,list.size());

		DaoHelper daoHelper = appInjector.getInstance(DaoHelper.class);
	}
}
