package org.projectmvc.entity;


import org.projectmvc.access.UserAccessContext;

public class User extends OrgScopedEntity<Long> {

	//// persistent properties
	private String username;
	private String pwd;
	private String firstName;
	private String lastName;
	private String photoUrl;

	// denote if it is an admin user
	private Boolean admin = false;

	//// per request properties
	private UserAccessContext userAccessContext;

	public User(){};

	public User(String username, String pwd){
		setUsername(username);
		setPwd(pwd);
	}

	public User(String userName, Long orgId){
		setUsername(userName);
		setOrgId(orgId);
	}

	public String getDisplayName(){
		StringBuilder sb = new StringBuilder();
		if (firstName != null){
			sb.append(firstName).append(" ");
		}
		if (lastName != null){
			sb.append(lastName);
		}
		if (firstName == null && lastName == null){
			sb.append(username);
		}
		return sb.toString();
	}

	public UserAccessContext getUserAccessContext() {
		return userAccessContext;
	}

	public void setUserAccessContext(UserAccessContext userAccessContext) {
		this.userAccessContext = userAccessContext;
	}

	// --------- Persistent Properties --------- //
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}


	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	
	
	// --------- /Persistent Properties --------- //
	
	
}
