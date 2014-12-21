package org.projectmvc.test;

import org.j8ql.query.InsertQuery;
import org.junit.Before;
import org.junit.BeforeClass;

import com.britesnow.snow.testsupport.SnowTestSupport;
import org.projectmvc.dao.DaoHelper;
import org.projectmvc.entity.User;

import java.util.Map;

import static com.britesnow.snow.util.MapUtil.mapIt;
import static org.j8ql.query.Query.insert;
import static org.j8ql.query.Query.select;

public class BaseTestSupport  extends SnowTestSupport{
	static protected Map defaultTestUsernamePassword = mapIt("username","test_user-01","pwd","welcome");
	static protected Object[][] testUsers = {{"test_user-01","welcome"},{"test_user-02","welcome"}};
	static private InsertQuery<User> testUserInsert = insert("user").columns("username", "pwd").returning(User.class);

    @BeforeClass
    public static void initTestClass() throws Exception {
        SnowTestSupport.initWebApplication("src/main/webapp");
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
		daoHelper.executeUpdate("delete from \"user\" where username like 'test_%'");

		//da.create("user", defaultTestUsernamePassword);
	}

	protected User createTestUser1(){
		DaoHelper daoHelper = appInjector.getInstance(DaoHelper.class);
		return daoHelper.execute(testUserInsert.values(testUsers[0]));
	}

	protected User createTestUser2(){
		DaoHelper daoHelper = appInjector.getInstance(DaoHelper.class);
		return daoHelper.execute(testUserInsert.values(testUsers[1]));
	}




    
}
