package org.projectmvc.entity;


import java.util.Date;

public class Project extends OrgScopedEntity<Long> {

    private String name;
    private String description;
    
    public Project(){
    }

    public Project(String name, Long orgId){
        setName(name);
        setOrgId(orgId);
    }

    public Project(String name, String description){
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}