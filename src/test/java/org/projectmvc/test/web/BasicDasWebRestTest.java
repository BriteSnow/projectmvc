package org.projectmvc.test.web;

import com.britesnow.snow.testsupport.mock.RequestContextMock;
import com.britesnow.snow.util.JsonUtil;
import org.junit.Test;
import org.projectmvc.dao.DaoRegistry;
import org.projectmvc.dao.IDao;
import org.projectmvc.entity.Project;
import org.projectmvc.test.BaseTestSupport;

import java.util.List;
import java.util.Map;

import static com.britesnow.snow.util.MapUtil.getDeepValue;
import static com.britesnow.snow.util.MapUtil.mapIt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by jeremychone on 12/27/13.
 */
public class BasicDasWebRestTest extends BaseTestSupport {


	@Test
	public void simpleProjectCreateWebRestTest(){
		createTestUser1();

		String projectName = "test_project_single";

		Map authCookieMap = doPost("/login",defaultTestUsernamePassword).getCookieMap();

		String projectJsonStr = JsonUtil.toJson(mapIt("name", projectName));
		RequestContextMock rc = doPost("/das-create-project",mapIt("props",projectJsonStr), authCookieMap);

		// get and check the result
		Map result = rc.getResponseAsJson();

		assertEquals(projectName, getDeepValue(result, "result.name"));
	}

	/**
	 * TODO: once this security layer will be put in place, this unit test will have to change.
	 */
	@Test
	public void simpleProjectCreateAndGetWithoutAuth() {
		// create the project directly in the db using DAO
		DaoRegistry daoRegistry = appInjector.getInstance(DaoRegistry.class);
		IDao<Project, Long> projectDao = daoRegistry.getDao(Project.class);
		Project project = new Project();
		String projectName = "test_project-001";
		project.setName(projectName);
		Long projectId = (Long) projectDao.create(null, project);

		Map result, params, projectMap;

		// Test getting with a json id param
		// get the first project via rest
		params = mapIt("id", JsonUtil.toJson(mapIt("id", projectId)));
		result = doGet("/das-get-project", params).getResponseAsJson();
		projectMap = (Map) result.get("result");
		assertEquals(projectId.intValue(), projectMap.get("id"));
		assertEquals(projectName, projectMap.get("name"));

		// Test getting with a direct id value
		result = doGet("/das-get-project", mapIt("id", "" + projectId)).getResponseAsJson();
		projectMap = (Map) result.get("result");
		assertEquals(projectId.intValue(), projectMap.get("id"));
		assertEquals(projectName, projectMap.get("name"));
	}


	// FIXME: kind of weird, on mvn clean package, this method hang on getting a connection?
	//        works fine when running this unit test in IntelliJ ?
	//@Test
	public void projectsCrudWebRestTest(){
		Map authCookieMap = doPost("/login",defaultTestUsernamePassword).getCookieMap();

		// create 9 projects
		for (int i = 0 ; i < 10 ; i++){
			// put half with "A" and the other one with "B" (to test like later)
			String projectName =  "test_project" + ((i<5)?"A":"B") + "_" + i;

			String projectJsonStr = JsonUtil.toJson(mapIt("name", projectName));
			RequestContextMock rc = doPost("/das-create-project",mapIt("props",projectJsonStr), authCookieMap);

			// get and check the result
			Map result = rc.getResponseAsJson();
			assertTrue((Boolean) result.get("success"));
			// check that the name match.
			assertEquals(projectName, getDeepValue(result, "result.name"));
			// check that the id is a number (MapUtil.getDeepValue... would return null if not)
			assertNotNull(getDeepValue(result,"result.id",Integer.class));
		}

		// list all the projects
		List list = (List) doGet("/das-list-project",null,authCookieMap).getResponseAsJson().get("result");
		assertEquals(10, list.size());

		// get the first project from the list
		Map firstProjectMap = (Map) list.get(0);
		// get the the id of the first one.
		Object firstProjectId = firstProjectMap.get("id");

		// get the first project via rest
		Map projectMap = (Map) doGet("/das-get-project",mapIt("id","" + firstProjectId),authCookieMap).getResponseAsJson().get("result");
		assertEquals(firstProjectId,projectMap.get("id"));

		// list the project with the "test_projectA" prefix
		Map param = mapIt("filter", JsonUtil.toJson(mapIt("name,like", "test_projectA%")));
		list = (List) doGet("/das-list-project",param,authCookieMap).getResponseAsJson().get("result");
		assertEquals(5,list.size());
	}

}
