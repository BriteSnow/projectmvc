package org.projectmvc.access;

import static org.projectmvc.access.ProjectPrivilege.*;

/**
 * Created by jeremychone on 3/1/14.
 */
public enum ProjectRole {

	owner(ProjectPrivilege.values()),
	developer(create_tickets),
	lead_developer(create_tickets, update_others_tickets, resolve_others_tickets),
	qa(create_tickets,update_others_tickets, resolve_others_tickets);

	private ProjectPrivilege[] projectPrivileges;

	ProjectRole(ProjectPrivilege... projectPrivileges){
		this.projectPrivileges = projectPrivileges;
	}

	public ProjectPrivilege[] getProjectPrivileges(){
		return projectPrivileges;
	}
}
