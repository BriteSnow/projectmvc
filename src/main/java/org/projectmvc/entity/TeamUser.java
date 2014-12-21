package org.projectmvc.entity;


public class TeamUser extends BaseEntity<TeamUser.Id>{


    private Team.Role role;


	// --------- Composite ID --------- //
	public Long getTeamId() {
		return (getId() == null)?null:getId().teamId;
    }
    public void setTeamId(Long teamId) {
		getOrCreateId().teamId = teamId;
    }

    public Long getUserId() {
		return (getId() == null)?null:getId().userId;
    }
    public void setUserId(Long userId) {
		getOrCreateId().userId = userId;
    }

	private Id getOrCreateId(){
		Id id = getId();
		if (id == null){
			id = new Id();
			setId(id);
		}
		return id;
	}
	// --------- /Composite ID --------- //


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
