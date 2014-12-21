package org.projectmvc.dao;

import com.google.inject.Singleton;
import org.j8ql.query.Query;
import org.j8ql.query.Condition;
import org.j8ql.query.SelectQuery;
import org.projectmvc.entity.Project;
import org.projectmvc.entity.Team;
import org.projectmvc.entity.TeamUser;
import org.projectmvc.entity.User;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Singleton
public class ProjectDao extends BaseDao<Project, Long> {

	@Inject
	IDao<Team,Long> teamDao;

	@Inject
	IDao<TeamUser,TeamUser.Id> teamUserDao;

	// --------- Create --------- //
	@Override
	public Long create(User user, Project entity) {
		Long projectId = super.create(user, entity);
		createOwnerTeam(user,projectId);
		return projectId;
	}

	@Override
	public Long create(User user, Map map) {
		Long projectId = super.create(user, map);
		createOwnerTeam(user,projectId);
		return projectId;
	}

	private void createOwnerTeam(User user, Long projectId){
		// TODO: for now allow to create project without owner, but might change.
		if (user != null){
			Team team = new Team();
			team.setName("owner");
			team.setProjectId(projectId);
			Long teamId = teamDao.create(user, team);

			TeamUser teamUser = new TeamUser();
			teamUser.setTeamId(teamId);
			teamUser.setUserId(user.getId());
			teamUserDao.create(user, teamUser);
		}
	}
	// --------- /Create --------- //


	@Override
	public List<Project> list(User user, Condition filter, int pageIdx, int pageSize, String... orderBy) {
		// get the basic list select
		SelectQuery<Project> select = listSelectBuilder(user,filter,pageIdx,pageSize,orderBy);
		if (user != null){
			// add the inner join.
			select = select.innerJoin("team","projectId","project","id").innerJoin("teamuser","teamId","team","id");

			Condition userCondition = Query.and("teamuser.userId",user.getId());

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
