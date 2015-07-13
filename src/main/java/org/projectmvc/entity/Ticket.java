package org.projectmvc.entity;


public class Ticket extends OrgScopedEntity<Long> {

    private Long projectId;
    private String title;
    private String desc;

    public Ticket(){}

	public Ticket(String title, Long orgId) {
		setTitle(title);
		setOrgId(orgId);
	}

	public Long getProjectId() {
        return projectId;
    }
    public Ticket setProjectId(Long projectId) {
        this.projectId = projectId;
		return this;
    }

    public String getTitle() {
        return title;
    }
    public Ticket setTitle(String title) {
        this.title = title;
		return this;
    }

	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
