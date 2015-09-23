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

	/**
	 * Currently, the pattern is that any user added to the org = 1 will be sysadmin.
	 *
	 * This pattern enable the following:
	 *
	 * 	- Avoid to have uncessary flags on user or org entities that 99.99999% of the case will be false.
	 * 	- Allow to have multiple sysadmin accounts (and eventually have different privileges for them)
	 * 	- Still safe, as the org = 1 should be created a database initial seed.
	 */
	public boolean isSysAdmin(){
		if (getOrgId() != null && getOrgId() == 1){
			return true;
		}
		return false;
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
	// --------- /Persistent Properties --------- //
	
	
}
