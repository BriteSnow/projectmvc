package org.projectmvc.test;

import org.junit.Test;
import org.projectmvc.access.AccessManager;
import org.projectmvc.dao.DaoRegistry;
import org.projectmvc.dao.IDao;
import org.projectmvc.dao.ProjectDao;
import org.projectmvc.dao.UserDao;
import org.projectmvc.entity.*;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.j8ql.query.Query.and;
import static org.jomni.util.Maps.mapOf;
import static org.junit.Assert.assertEquals;

public class DaoTest extends BaseTestSupport {

	//@Test
	public void simpleTicketCreateTest() {
		DaoRegistry daoRegistry = appInjector.getInstance(DaoRegistry.class);
		IDao<Ticket,Long> ticketDao = daoRegistry.getDao(Ticket.class);

		Ticket ticket = new Ticket();
		ticket.setTitle("test_DaoTest-simpleTicketCreateTest");
		ticket.setOrgId(0L);
		Long ticketId = ticketDao.create(null, ticket);

		Ticket ticketReloaded = ticketDao.get(null, ticketId).get();
		assertEquals("test_DaoTest-simpleTicketCreateTest",ticketReloaded.getTitle());
	}

	@Test
	public void createWithMapAndExtraProperty(){
		ProjectDao projectDao = appInjector.getInstance(ProjectDao.class);
		User user = createTestUser1();
		Map projectMap = mapOf("name", "test_createWithMapAndExtraProperty", "orgId", user.getOrgId());
		projectMap.put("foo", "extra property");

		Long projectId = projectDao.create(user, projectMap);

		assertEquals("test_createWithMapAndExtraProperty", projectDao.get(user, projectId).get().getName());
	}

	@Test
	public void simpleTeamCreateTest() {
		DaoRegistry daoRegistry = appInjector.getInstance(DaoRegistry.class);
		IDao<Team,Long> teamDao = daoRegistry.getDao(Team.class);

		Team team = new Team();
		team.setName("test_devtest-simpleTest");
		team.setOrgId(123L);
		Long teamId = teamDao.create(null, team);

		Team teamReloaded = teamDao.get(null, teamId).get();
		assertEquals("test_devtest-simpleTest",teamReloaded.getName());
	}

	//@Test
	public void simpleCreateAndList(){
		DaoRegistry daoRegistry = appInjector.getInstance(DaoRegistry.class);
		IDao<Ticket,Long> ticketDao = daoRegistry.getDao(Ticket.class);

		ticketDao.create(null, new Ticket("test_ticket-A-01", 123L));
		ticketDao.create(null, new Ticket("test_ticket-B-03", 123L));
		ticketDao.create(null, new Ticket("test_ticket-B-03", 123L));
		ticketDao.create(null, new Ticket("test_ticket-B-03", 123L));

		List<Ticket> tickets = ticketDao.list(null,and("title;ilike","test_%-B-%"),0,100);
		assertEquals(3, tickets.size());
	}


    @Test
    public void testProjectTicketDaos() {
        DaoRegistry daoRegistry = appInjector.getInstance(DaoRegistry.class);

		User user = createTestUser1();

        IDao<Project,Long> projectDao = daoRegistry.getDao(Project.class);
		IDao<Ticket,Long> ticketDao = daoRegistry.getDao(Ticket.class);

        Long projectId = projectDao.create(user, new Project("test_project-01", user.getOrgId()));

		Ticket ticket = new Ticket();
		ticket.setProjectId(projectId);
		ticket.setOrgId(user.getOrgId());
		ticket.setTitle("test_ticket-01");
		Long ticketId = ticketDao.create(null, ticket);

		List<Ticket> tickets = ticketDao.list(null,and("projectId",projectId),0,100 );
		assertEquals(1, tickets.size());
	}

	@Test
	public void testProjectWithUserTeam(){
		User user1 = createTestUser1();
		User user2 = createTestUser2();

		DaoRegistry daoRegistry = appInjector.getInstance(DaoRegistry.class);
		IDao<Project,Long> projectDao = daoRegistry.getDao(Project.class);

		// user1 has 2 projects
		projectDao.create(user1, new Project("test_project-01", user1.getOrgId()));
		projectDao.create(user1, new Project("test_project-02", user1.getOrgId()));

		// user2 has one project
		projectDao.create(user2, new Project("test_project-02", user2.getOrgId()));


		List<Project> projects = projectDao.list(user1, null, 0, 100);
		assertEquals(2, projects.size());

		projects = projectDao.list(user2, null, 0, 100);
		assertEquals(1, projects.size());


	}



}
