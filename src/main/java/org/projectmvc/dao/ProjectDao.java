package org.projectmvc.dao;

import com.google.inject.Singleton;
import org.j8ql.query.Query;
import org.j8ql.query.Condition;
import org.j8ql.query.SelectQuery;
import org.projectmvc.access.OrgPrivilege;
import org.projectmvc.access.ProjectPrivilege;
import org.projectmvc.access.annotation.AssertParamOrgPrivileges;
import org.projectmvc.access.annotation.AssertParamProjectPrivileges;
import org.projectmvc.access.annotation.AssertReturnProjectPrivileges;
import org.projectmvc.entity.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * ProjectDao subclass BaseDao for the following reasons:
 *
 * - to override doCreate method to create the defaultOwnerTeam
 * - to add the @RequireOrgPrivileges to add privileges
 *
 * TODO: we will need to add the @RequireOrgPrivileges for list Project as well.
 */
@Singleton
public class ProjectDao extends BaseDao<Project, Long> {

	@Inject
	IDao<Team,Long> teamDao;

	@Inject
	IDao<TeamUser,TeamUser.Id> teamUserDao;


	public ProjectDao(){
		defaultOrderBy = new String[]{"name"};
	}

	// --------- Create --------- //


	/**
	 * TODO: need to add the @RequireProjectPrivileges
	 * @param user
	 * @param id
	 * @return
	 */
	@Override
	@AssertReturnProjectPrivileges(ProjectPrivilege.view)
	public Optional<Project> get(User user, Long id) {
		return super.get(user, id);
	}

	@Override
	@AssertParamOrgPrivileges(OrgPrivilege.CREATE_PROJECT)
	protected Long doCreate(User user, Project newEntity, Set<Object> columns) {
		Long projectId = super.doCreate(user, newEntity, columns);
		createOwnerTeam(user,projectId);
		return projectId;
	}

	private void createOwnerTeam(User user, Long projectId) {
		// TODO: for now allow to create project without owner, but might change.
		Project project = get(user, projectId).get();
		if (user != null) {
			Team team = new Team();
			team.setName("owner");
			team.setProjectId(projectId);
			team.setOrgId(project.getOrgId());
			Long teamId = teamDao.create(user, team);

			TeamUser teamUser = new TeamUser();
			teamUser.setTeamId(teamId);
			teamUser.setUserId(user.getId());
			teamUserDao.create(user, teamUser);
		}
	}
	// --------- /Create --------- //



	@Override
	@AssertReturnProjectPrivileges(ProjectPrivilege.view)
	public List<Project> list(User user, Condition filter, int pageIdx, int pageSize, String... orderBy) {
		// get the basic list select
		SelectQuery<Project> select = listSelectBuilder(user, filter, pageIdx, pageSize, orderBy);
		if (user != null) {
			// add the inner join.
			select = select.innerJoin("team", "projectId", "project", "id").innerJoin("teamuser", "teamId", "team", "id");

			Condition userCondition = Query.and("teamuser.userId", user.getId());

			Condition where = select.getWhere();
			where = (where == null) ? userCondition : where.and(userCondition);
			select = select.where(where);
		}
		return daoHelper.list(select);
	}


	// TODO: need to add the list
	//final String projectSelect = "select project.* from project" +
	//		" inner join team on project.id = team.\"projectId\"" +
	//		" inner join teamuser on team.id = teamuser.\"teamId\"";

}
