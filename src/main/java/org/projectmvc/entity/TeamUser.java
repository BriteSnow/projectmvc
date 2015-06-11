package org.projectmvc.entity;


public class TeamUser extends BaseEntity<TeamUser.Id>{


    private Team.Role role;


	// --------- Entity Composite ID Accessors --------- //
	public Long getTeamId() {
		return getOptionalId().map(Id::getTeamId).orElse(null);
    }
    public void setTeamId(Long teamId) {
		getOptionalId().orElseGet(() -> setIdAndReturnId(new Id())).setTeamId(teamId);
    }

    public Long getUserId() {
		return getOptionalId().map(Id::getUserId).orElse(null);
	}
	public void setUserId(Long userId) {
		getOptionalId().orElseGet(() -> setIdAndReturnId(new Id())).setUserId(userId);
    }
	// --------- /Entity Composite ID Accessors --------- //

    public Team.Role getRole() {
        return role;
    }
    public void setRole(Team.Role role) {
        this.role = role;
    }

	public static class Id{
		private Long teamId;
		private Long userId;

		public Id(){};

		public Id(Long teamId, Long userId){
			this.teamId = teamId;
			this.userId = userId;
		}

		public Long getTeamId() {
			return teamId;
		}

		public void setTeamId(Long teamId) {
			this.teamId = teamId;
		}

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}
	}
}
