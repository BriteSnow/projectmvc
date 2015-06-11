package org.projectmvc.test.web;

import com.britesnow.snow.testsupport.mock.RequestContextMock;
import com.britesnow.snow.util.JsonUtil;
import com.google.inject.Inject;
import org.j8ql.query.Query;
import org.junit.Test;
import org.projectmvc.dao.DaoHelper;
import org.projectmvc.entity.User;
import org.projectmvc.test.BaseTestSupport;

import java.util.Map;

import static com.britesnow.snow.util.MapUtil.mapIt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RegisterWebTest extends BaseTestSupport {

	@Test
	public void registerUser() {


		// register the user
		Map user1CookieMap = doPost("/register", mapIt("username", "test_user_projectAndTeam", "pwd", "welcome", "pwdRepeat", "welcome")).getCookieMap();

		// check in the database if the data was set
		DaoHelper daoHelper = appInjector.getInstance(DaoHelper.class);
		User user = daoHelper.first(Query.select(User.class).where("username;like","test_%")).get();
		assertEquals("test_user_projectAndTeam", user.getUsername());
		assertNotNull(user.getOrgId());
	}
}
