package org.projectmvc.access;

/**
 * Created by jeremychone on 3/1/14.
 */
public enum ProjectPrivilege {
	// This is the minimum privilege allow to view, comment, and assign labels to tickets.
	view,
	create_tickets,
	update_others_tickets,
	resolve_others_tickets,
	close_others_tickets,
	manage_labels;
}
