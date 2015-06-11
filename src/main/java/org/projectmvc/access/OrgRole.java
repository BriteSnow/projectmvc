package org.projectmvc.access;

public enum OrgRole {

	owner(OrgPrivilege.values());

	private OrgPrivilege[] orgPrivileges;

	OrgRole(OrgPrivilege... orgPrivileges) {
		this.orgPrivileges = orgPrivileges;
	}

	public OrgPrivilege[] getOrgPrivileges() {
		return orgPrivileges;
	}
}
