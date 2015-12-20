package org.projectmvc.entity;

public class OrgUser extends BaseEntity<OrgUser.Id>{

	private String roles;

	// --------- Entity Composite ID Accessors --------- //
	public Long getOrgId() {
		return getOptionalId().map(Id::getOrgId).orElse(null);
	}

	public void setOrgId(Long orgId) {
		getOptionalId().orElseGet(() -> setIdAndReturnId(new Id())).setOrgId(orgId);
	}

	public Long getUserId() {
		return getOptionalId().map(Id::getUserId).orElse(null);
	}

	public void setUserId(Long userId) {
		getOptionalId().orElseGet(() -> setIdAndReturnId(new Id())).setUserId(userId);
	}
	// --------- /Entity Composite ID Accessors --------- //

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public static class Id{
		private Long orgId;
		private Long userId;

		public Id() {}

		public Id(Long orgId, Long userId) {
			this.orgId = orgId;
			this.userId = userId;
		}

		public Long getOrgId() {
			return orgId;
		}

		public void setOrgId(Long orgId) {
			this.orgId = orgId;
		}

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}
	}
}
