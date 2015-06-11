package org.projectmvc.entity;

public class Org extends BaseEntity<Long>{

	private String name;
	private Boolean personal = false;

	public Org(){

	}

	public Org(String name) {
		this.name = name;
	}

	// --------- Persistent Property Accessors --------- //
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getPersonal() {
		return personal;
	}

	public void setPersonal(Boolean personal) {
		this.personal = personal;
	}
	// --------- /Persistent Property Accessors --------- //

}
