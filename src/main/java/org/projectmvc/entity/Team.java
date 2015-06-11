package org.projectmvc.entity;


public class Team extends OrgScopedEntity<Long> {

    public enum Role{
        owner, admin, manager, contributor, observer;
    }
    
    private String name;
    private Long projectId;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getProjectId() {
        return projectId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }


}
