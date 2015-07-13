package org.projectmvc.entity;


public class Project extends OrgScopedEntity<Long> {

    private String name;
    private String desc;
    
    public Project(){
    }

    public Project(String name, Long orgId){
        setName(name);
        setOrgId(orgId);
    }

    public Project(String name, String desc){
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}