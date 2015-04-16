package org.projectmvc.test;

import org.junit.Test;
import org.projectmvc.dao.DaoRegistry;
import org.projectmvc.dao.IDao;
import org.projectmvc.entity.*;

import java.util.List;

import static org.j8ql.query.Query.and;
import static org.junit.Assert.assertEquals;

public class DaoTest extends BaseTestSupport {

    @Test
    public void emptyTest() {

    }
	@Test
	public void simpleTicketCreateTest() {
		DaoRegistry daoRegistry = appInjector.getInstance(DaoRegistry.class);
		IDao<Ticket,Long> ticketDao = daoRegistry.getDao(Ticket.class);

		Ticket ticket = new Ticket();
		ticket.setTitle("test_devtest-simpleTest");
		Long ticketId = ticketDao.create(null, ticket);

		Ticket ticketReloaded = ticketDao.get(null, ticketId).get();
		assertEquals("test_devtest-simpleTest",ticketReloaded.getTitle());
	}

	@Test
	public void simpleTeamCreateTest() {
		DaoRegistry daoRegistry = appInjector.getInstance(DaoRegistry.class);
		IDao<Team,Long> teamDao = daoRegistry.getDao(Team.class);

		Team team = new Team();
		team.setName("test_devtest-simpleTest");
		Long teamId = teamDao.create(null, team);

		Team teamReloaded = teamDao.get(null, teamId).get();
		assertEquals("test_devtest-simpleTest",teamReloaded.getName());
	}

	@Test
	public void simpleCreateAndList(){
		DaoRegistry daoRegistry = appInjector.getInstance(DaoRegistry.class);
		IDao<Ticket,Long> ticketDao = daoRegistry.getDao(Ticket.class);

		ticketDao.create(null,new Ticket().setTitle("test_ticket-A-01"));
		ticketDao.create(null,new Ticket().setTitle("test_ticket-B-03"));
		ticketDao.create(null,new Ticket().setTitle("test_ticket-B-03"));
		ticketDao.create(null,new Ticket().setTitle("test_ticket-B-03"));

		List<Ticket> tickets = ticketDao.list(null,and("title;ilike","test_%-B-%"),0,100);
		assertEquals(3, tickets.size());
	}


    @Test
    public void testProjectTicketDaos() {
        DaoRegistry daoRegistry = appInjector.getInstance(DaoRegistry.class);

		User user = createTestUser1();

        IDao<Project,Long> projectDao = daoRegistry.getDao(Project.class);
		IDao<Ticket,Long> ticketDao = daoRegistry.getDao(Ticket.class);

        Project project = new Project();
        project.setName("test_project-01");
        Long projectId = projectDao.create(user, project);

		Ticket ticket = new Ticket();
		ticket.setProjectId(projectId);
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
		Project project = new Project();
		project.setName("test_project-01");
		projectDao.create(user1, project);

		project = new Project();
		project.setName("test_project-02");
		projectDao.create(user1, project);

		// user2 has one project
		project = new Project();
		project.setName("test_project-03");
		projectDao.create(user2, project);


		List<Project> projects = projectDao.list(user1, null, 0, 100);
		assertEquals(2, projects.size());

		projects = projectDao.list(user2, null, 0, 100);
		assertEquals(1, projects.size());


	}
}
