package org.projectmvc.dao;

import com.google.inject.Inject;
import org.projectmvc.entity.Ticket;
import org.projectmvc.entity.User;

import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class TicketDao extends BaseDao<Ticket, Long> {

	@Inject
	ProjectDao projectDao;

	@Override
	protected Long doCreate(User user, Ticket newEntity, Set<String> columns) {
		Long orgId = projectDao.getWithColumns(user, newEntity.getProjectId(), "orgId").map(p -> p.getOrgId()).orElse(null);
		newEntity.setOrgId(orgId);
		Set<String> newColumns = null;
		if (columns != null) {
			newColumns = new HashSet<>();
			newColumns.addAll(columns);
			newColumns.add("orgId");
		}
		return super.doCreate(user, newEntity, newColumns);
	}
}
