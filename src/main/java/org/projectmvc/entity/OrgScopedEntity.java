package org.projectmvc.entity;

public abstract class OrgScopedEntity<I> extends BaseEntity<I> {

	//// persistent properties
	private Long orgId;

	// --------- Persistent Property Accessors --------- //
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	// --------- /Persistent Property Accessors --------- //


}
