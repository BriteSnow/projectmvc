package org.projectmvc.access;

import org.jomni.JomniBuilder;
import org.jomni.JomniMapper;
import org.projectmvc.AppException;
import org.projectmvc.access.UserAccessContext.Access;
import org.projectmvc.dao.IDao;
import org.projectmvc.dao.UserDao;
import org.projectmvc.entity.OrgUser;
import org.projectmvc.entity.Project;
import org.projectmvc.entity.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

/**
 * This is the singleton manager that is responsible to assert all type of privileges.
 *
 * It uses what is stored in the UserAccessContext and if not found there, try to load it, and put it back in the
 * UserAccessContext.
 *
 * The goal of the UserAccessContext is to store the process privileged by object for a Request, this way, we do not have to
 * reload/recompute privileges.
 *
 * In fact, DAOs list and AppAuth, can add more information to the UserAccessContext when it loads data from the DB, this way, it might save
 * future access check since it will be already in the UserAccessContext.
 *
 */
@Singleton
public class AccessManager {

	@Inject
	UserDao userDao;

	JomniMapper jomni = new JomniBuilder().build();

	@Inject
	IDao<OrgUser,OrgUser.Id> orgUserDao;

	@Inject
	IDao<Project, Long> projectDao;

	/**
	 * This initialize and set the UserAccessContext to this user for future use.
	 *
	 * Usually called from the AppAuth, but can be called in unit testing as well.
	 *
	 * @param user
	 */
	public void initUserAccessContext(User user){
		// create and set the UserAccessContext
		UserAccessContext uac = new UserAccessContext(user.getId());
		// we can add the owner privileges for the user org (this way we will have it in the uac, if some check needs to happen)
		uac.addOrgPrivileges(user.getOrgId(), OrgRole.owner.getOrgPrivileges());
		// set set this UserAccessContext in this User object for this request session.
		// Obviously, this should not be saved in the DB, and just a way to keep the request user context accross this request session (not httpsession)
		user.setUserAccessContext(uac);
	}


	// --------- Project Privilege assertion --------- //
	public void assertProjectPrivilege(UserAccessContext uac, Project project, ProjectPrivilege... privileges) {
		// First check if we already computed it
		Access access = uac.checkProjectAccess(project.getId(), privileges);

		// if not, then, we load more
		if (Access.UNKOWN == access) {
			loadProjectPrivilege(uac, project);
		}

		// get access again
		access = uac.checkProjectAccess(project.getId(), privileges);

		// TODO: do the full check. Need to put a Logic.Warning if still Access.UNKNOW
		switch (access) {
			case APPROVED:
				return;
			case DENIED:
			case UNKOWN:
				throw new AppException(AccessError.FAILED_PROJECT_ACCESS,uac.getUserId(), project.getId(), privileges);
		}

	}

	private void loadProjectPrivilege(UserAccessContext uac, Project project){
		// First, very simple security, only the owner of the project can have access (we will add a way to have project roles with team later)
		Long cid = project.getCid();
		if (cid != null && cid.equals(uac.getUserId())){
			uac.addProjectPrivileges(project.getId(), ProjectRole.owner.getProjectPrivileges());
		}

		// TODO: Later, if the user is not the owner/creator, then, need to look at the Team for this project and user, to see the roles, and make a
		//       Union of the roles, and then, privileges.
	}
	// --------- /Project Privilege assertion --------- //

	// --------- Org Privilege Assertions --------- //
	public void assertOrgPrivilege(UserAccessContext uac, Long orgId,  OrgPrivilege... privileges){
		// First check if we already computed it
		Access access = uac.checkOrgAccess(orgId, privileges);

		// if not, then, we load more
		if (Access.UNKOWN == access) {
			loadOrgPrivilege(uac, orgId);
		}

		// get access again
		access = uac.checkOrgAccess(orgId, privileges);

		// TODO: do the full check. Need to put a Logic.Warning if still Access.UNKNOW
		switch (access) {
			case APPROVED:
				return;
			case DENIED:
			case UNKOWN:
				throw new AppException(AccessError.FAILED_ORG_ACCESS,uac.getUserId(), orgId, privileges);
		}
	}

	private void loadOrgPrivilege(UserAccessContext uac, Long orgId){
		Optional<OrgUser> orgUser = orgUserDao.get(null,new OrgUser.Id(orgId,uac.getUserId()));
		if (orgUser.isPresent()) {
			// assuming single role for now (lated, we should split the orgRoles by "," if multiple)
			OrgRole orgRole = jomni.as(OrgRole.class,orgUser.get().getOrgRoles());
			uac.addOrgPrivileges(orgId, orgRole.getOrgPrivileges());
		}else{
			// we init with no privileges (which basically make it Access.Denied next time)
			uac.addOrgPrivileges(orgId, null);
		}
	}
	// --------- /Org Privilege Assertions --------- //

}
