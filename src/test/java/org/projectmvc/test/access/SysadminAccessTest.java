package org.projectmvc.test.access;

import com.britesnow.snow.testsupport.mock.RequestContextMock;
import com.britesnow.snow.util.JsonUtil;
import org.junit.Test;
import org.projectmvc.dao.DaoHelper;
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
import static org.junit.Assert.assertTrue;

public class SysadminAccessTest extends BaseTestSupport {

	@Test
	public void testGetPerfAll(){

		Map cookie, response;

		// --------- Test Perf Access from Sysadmin (should succeed) --------- //
		cookie = doLogin("sysadmin","welcome"); // this user is seeded at database setup

		response = doGet("/perf-get-all",null, cookie).getResponseAsJson();

		assertTrue("response.success should be true", (Boolean) response.get("success"));
		// --------- /Test Perf Access from Sysadmin (should succeed) --------- //


		// --------- Test with User1 (should fail) --------- //
		User user1 = createTestUser1();

		cookie = doLogin(user1.getUsername(), user1.getPwd());

		response = doGet("/perf-get-all",null, cookie).getResponseAsJson();

		assertFalse("response.success should be false", (Boolean) response.get("success"));
		// --------- /Test with User1 (should fail) --------- //

	}



}
