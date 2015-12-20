package org.projectmvc.access;

public enum SystemRole {

	full(SystemPrivilege.values());

	private SystemPrivilege[] systemPrivileges;

	SystemRole(SystemPrivilege... systemPrivileges) {
		this.systemPrivileges = systemPrivileges;
	}

	public SystemPrivilege[] getOrgPrivileges() {
		return systemPrivileges;
	}
}
