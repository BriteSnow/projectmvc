package org.projectmvc.test.access;

import com.britesnow.snow.testsupport.mock.RequestContextMock;
import com.britesnow.snow.util.JsonUtil;
import org.junit.Test;
import org.projectmvc.dao.DaoHelper;
import org.projectmvc.dao.DaoRegistry;
import org.projectmvc.dao.OrgDao;
import org.projectmvc.dao.UserDao;
import org.projectmvc.entity.Org;
import org.projectmvc.entity.User;
import org.projectmvc.test.BaseTestSupport;

import java.util.Map;

import static com.britesnow.snow.util.MapUtil.mapIt;
import static org.jomni.util.Maps.nestedValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DaoAccessTest extends BaseTestSupport {

	@Test
	public void testOrgProjectCreateAccess(){

		DaoHelper daoHelper = appInjector.getInstance(DaoHelper.class);
		OrgDao orgDao = appInjector.getInstance(OrgDao.class);
		UserDao userDao = appInjector.getInstance(UserDao.class);

		// --------- Setup the User, UserOrg, and other Org --------- //
		// register a new user (which will have its own org)
		User user = userDao.createUser("test_testOrgAccess_user-1","welcome");

		// create a first org (which will be the outside org)
		Org otherOrg = new Org("test_testOrgAccess_org-2");
		Long otherOrgId = orgDao.create(null, otherOrg);

		// we assert that the two user org id different than the first one (must be the case)
		assertFalse(otherOrgId.equals(user.getOrgId()));

		// login with the user
		String projectJsonStr;
		RequestContextMock rc;
		Map responseAsJson;
		Map user1CookieMap = doPost("/login", mapIt("username", "test_testOrgAccess_user-1", "pwd", "welcome")).getCookieMap();
		// --------- /Setup the User, UserOrg, and other Org --------- //


		// --------- User1 try to create new Project in personal org (should pass) --------- //
		projectJsonStr = JsonUtil.toJson(mapIt("name", "test_testOrgAccess_project-1","orgId", "" + user.getOrgId()));
		rc = doPost("/das-create-project",mapIt("props",projectJsonStr), user1CookieMap);
		responseAsJson = rc.getResponseAsJson();
		assertEquals(true, responseAsJson.get("success"));
		// --------- /User1 try to create new Project in personal org (should pass) --------- //

		// --------- User1 try to create new Project in other org (should fail) --------- //
		projectJsonStr = JsonUtil.toJson(mapIt("name", "test_testOrgAccess_project-2", "orgId", "" + otherOrgId));
		rc = doPost("/das-create-project",mapIt("props",projectJsonStr,"orgId", "" + otherOrgId), user1CookieMap);
		responseAsJson = rc.getResponseAsJson();
		//System.out.println(responseAsJson);
		assertEquals(false,responseAsJson.get("success"));
		assertEquals("FAILED_ORG_ACCESS",responseAsJson.get("errorCode"));
		// --------- /User1 try to create new Project in other org (should fail) --------- //

	}

	@Test
	public void testOrgProjectGetAccess(){
		DaoHelper daoHelper = appInjector.getInstance(DaoHelper.class);
		OrgDao orgDao = appInjector.getInstance(OrgDao.class);
		UserDao userDao = appInjector.getInstance(UserDao.class);

		// --------- Setup the User, UserOrg, and other Org --------- //
		// register a new user (which will have its own org)
		User user1 = userDao.createUser("test_testOrgAccess_user-1","welcome");
		User user2 = userDao.createUser("test_testOrgAccess_user-2","welcome");

		// login with the user
		String projectJsonStr;
		RequestContextMock rc;
		Map responseAsJson;
		Map user1CookieMap = doPost("/login", mapIt("username", "test_testOrgAccess_user-1", "pwd", "welcome")).getCookieMap();
		Map user2CookieMap = doPost("/login", mapIt("username", "test_testOrgAccess_user-2", "pwd", "welcome")).getCookieMap();

		// create the project for user 1
		projectJsonStr = JsonUtil.toJson(mapIt("name", "test_testOrgAccess_project-1", "orgId", "" + user1.getOrgId()));
		rc = doPost("/das-create-project",mapIt("props",projectJsonStr), user1CookieMap);
		responseAsJson = rc.getResponseAsJson();
		Long projectId1 = jomni.as(Long.class, nestedValue(responseAsJson, "result.id"));
		assertEquals(true, responseAsJson.get("success"));

		// create the project for user 2
		projectJsonStr = JsonUtil.toJson(mapIt("name", "test_testOrgAccess_project-2", "orgId", "" + user1.getOrgId()));
		rc = doPost("/das-create-project",mapIt("props",projectJsonStr), user1CookieMap);
		responseAsJson = rc.getResponseAsJson();
		Long projectId2 = jomni.as(Long.class,nestedValue(responseAsJson,"result.id"));
		assertEquals(true, responseAsJson.get("success"));
		// --------- /Setup the User, UserOrg, and other Org --------- //

		// --------- Test if User 1 can get Project1 (should pass) --------- //
		rc = doGet("/das-get-project", mapIt("id", "" + projectId1), user1CookieMap);
		responseAsJson = rc.getResponseAsJson();
		assertEquals(true, responseAsJson.get("success"));
		// --------- /Test if User 1 can get Project1 (should pass) --------- //

	}

}
