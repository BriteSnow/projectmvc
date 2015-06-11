package org.projectmvc.access;

import org.projectmvc.entity.Org;
import org.projectmvc.entity.Project;

import java.util.*;

/**
 * This Object is created per request, by the AppAuthService (which populate it with available privileges), and then, used by the
 * AccessInterceptor and AccessManager to be queried and populated further.
 *
 * Note that this is also set to the User object from the Request @WebUser, which is passed to all Dao methods. This way,
 * later, we will be able to populate the UserAccessContext with some privileges as we load object. For Example,
 * when the ProjectDao list projects, we can do a join
 */
public class UserAccessContext {

	public enum Access{
		APPROVED, DENIED, UNKOWN
	}

	private final Long userId;


	private final Map<String, EntityAccess<ProjectPrivilege>> projectAccessByEntityKey = new HashMap<>();
	private final Map<String, EntityAccess<OrgPrivilege>> orgAccessByEntityKey = new HashMap<>();

	UserAccessContext(Long userId) {
		if (userId == null) {
			throw new RuntimeException("can't create a UserAccessContext with null userId");
		}
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

	// --------- Check Privilege --------- //
	public Access checkOrgAccess(Long orgId, OrgPrivilege... privileges){
		return checkAccess(OrgPrivilege.class, orgAccessByEntityKey, Org.class, orgId, privileges);
	}
	public Access checkProjectAccess(Long projectId, ProjectPrivilege... privileges){
		return checkAccess(ProjectPrivilege.class, projectAccessByEntityKey, Project.class, projectId, privileges);
	}

	private <P extends Enum> Access checkAccess(Class<P> privilegeClass, Map<String, EntityAccess<P>> entityAccessDic,
												Class entityClass,
											   	Object entityId,
												P... privileges){

		String key = entityKey(entityClass,entityId);
		EntityAccess<P> entityAccess = entityAccessDic.get(key);

		if (entityAccess == null){
			return Access.UNKOWN;
		}else {
			boolean pass = true;
			for (P privilege : privileges){
				pass = pass && entityAccess.isApproved(privilege);
				if (!pass){
					break;
				}
			}
			return (pass)?Access.APPROVED:Access.DENIED;
		}
	}
	// --------- /Check Privilege --------- //

	// --------- Add Approved Privileges --------- //
	void addProjectPrivileges(Long projectId, ProjectPrivilege... privileges) {
		addApprovePrivileges(ProjectPrivilege.class, projectAccessByEntityKey, Project.class, projectId , privileges);
	}

	void addOrgPrivileges(Long orgId, OrgPrivilege... privileges) {
		addApprovePrivileges(OrgPrivilege.class, orgAccessByEntityKey, Org.class, orgId, privileges);
	}

	private <P extends Enum> void addApprovePrivileges(Class<P> privilegeClass,
													   Map<String, EntityAccess<P>> entityAccessDic,
													   Class entityClass,
													   Object entityId,
													   P... privileges){
		// try to get the EntityAccess from the cache
		String key = entityKey(entityClass, entityId);
		EntityAccess<P> entityAccess = entityAccessDic.get(key);

		// if not, then, create and set it to the cache
		if (entityAccess == null){
			entityAccess = new EntityAccess<P>();
			entityAccessDic.put(key, entityAccess);
		}

		// if we have privileges, we add them to the entityAccess
		if (privileges != null){
			entityAccess.addApprove(privileges);
		}

		// Note that at this point, we will have an entityAccess for this entity in the cache,
		// and if it remains empty during the request session, it will mean that the user does not have access.
	}


	// --------- /Add Approved Privileges --------- //

	static private String entityKey(Class cls, Object id) {
		return cls.getSimpleName() + ":" + id;
	}

	public static class EntityAccess<P extends Enum>{
		Set<P> approvedSet = new HashSet<P>();

		public void addApprove(P... privileges) {
			approvedSet.addAll(Arrays.asList(privileges));
		}

		public boolean isApproved(P privilege) {
			return approvedSet.contains(privilege);
		}

	}
}


