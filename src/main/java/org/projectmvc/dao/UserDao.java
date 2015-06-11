package org.projectmvc.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.j8ql.query.Query;
import org.projectmvc.entity.Org;
import org.projectmvc.entity.User;

import static org.j8ql.query.Query.and;

@Singleton
public class UserDao extends BaseDao<User,Long> {

	@Inject
	private OrgDao orgDao;

	// TODO: needs to return Option<User>
	public User getByUsername(String username){
		return daoHelper.first(Query.select(entityClass).where("username", username)).orElse(null);
	}

	/**
	 * Higher level method to create a user and a new personal org.
	 *
	 * TODO: will need to add transactional @Transaction
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	public User createUser(String username, String password){
		Org org = new Org(username + "_org");
		org.setPersonal(true);
		Long orgId = orgDao.create(null, org);
		return createUser(username, password, orgId);
	}

	/**
	 * Higher level methods to create a user.
	 * @param username
	 * @param password
	 * @return
	 */
	public User createUser(String username, String password, Long orgId){
		 User user = new User();
		 user.setUsername(username);
		 user.setPwd(password);
		 user.setOrgId(orgId);
		 // for User, we can create new ones without an existing User
		 Long id = create(null, user);

		 return get(null,id).get();
	}
		
}
