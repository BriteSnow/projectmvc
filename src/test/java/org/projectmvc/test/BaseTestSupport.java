package org.projectmvc.test;

import org.jomni.JomniBuilder;
import org.jomni.JomniMapper;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.britesnow.snow.testsupport.SnowTestSupport;
import org.projectmvc.access.AccessManager;
import org.projectmvc.dao.DaoHelper;
import org.projectmvc.dao.UserDao;
import org.projectmvc.entity.User;

import java.util.Map;

import static com.britesnow.snow.util.MapUtil.mapIt;
import static org.j8ql.query.Query.insert;
import static org.j8ql.query.Query.select;

public class BaseTestSupport  extends SnowTestSupport{
	static protected Map<String,String> testUser1UsernamePwd = (Map<String, String>) mapIt("username","test_user-01","pwd","welcome");
	static protected Map<String,String> testUser2UsernamePwd = (Map<String, String>) mapIt("username","test_user-02","pwd","welcome");

	static public final JomniMapper jomni = new JomniBuilder().build();

    @BeforeClass
    public static void initTestClass() throws Exception {
        SnowTestSupport.initWebApplication("src/main/webapp");
    }

	@AfterClass
	public static void afterTestClass() throws Exception {
		DaoHelper daoHelper = appInjector.getInstance(DaoHelper.class);
		daoHelper.closeDataSource();
		SnowTestSupport.shutdownWebApplication();
	}

	@Before
	public void before(){
		cleanTables();
	}

	protected void cleanTables(){
		DaoHelper daoHelper = appInjector.getInstance(DaoHelper.class);


		daoHelper.executeUpdate("delete from project where name like 'test_%'");
		daoHelper.executeUpdate("delete from ticket where title like 'test_%'");
		daoHelper.executeUpdate("delete from team where name like 'test_%'");
		daoHelper.executeUpdate("delete from org where name like 'test_%'");
		daoHelper.executeUpdate("delete from \"user\" where username like 'test_%'");

		//da.create("user", testUser1UsernamePwd);
	}

	protected User createTestUser1() {
		UserDao userDao = appInjector.getInstance(UserDao.class);
		AccessManager accessManager = appInjector.getInstance(AccessManager.class);

		User user =  userDao.createUser(testUser1UsernamePwd.get("username"), testUser1UsernamePwd.get("pwd"));
		accessManager.initUserAccessContext(user);
		return user;
	}

	protected User createTestUser2() {
		UserDao userDao = appInjector.getInstance(UserDao.class);
		AccessManager accessManager = appInjector.getInstance(AccessManager.class);

		User user =  userDao.createUser(testUser2UsernamePwd.get("username"), testUser2UsernamePwd.get("pwd"));
		accessManager.initUserAccessContext(user);
		return user;
	}
}
