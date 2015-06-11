package org.projectmvc.access;

import org.projectmvc.ErrorType;

/**
 * This is the ErrorType for the Access package.
 */
public enum AccessError implements ErrorType {


	NO_ORG_IN_CONTEXT("No org in context for this user"),
	FAILED_ORG_ACCESS("User{%s} failed to access orgId{%s} for privileges %s"), // userId, orgId, privilege
	FAILED_PROJECT_ACCESS("User{%s} failed to access projectId{%s} for privileges %s"), // userId, projectId, privilege

	DAO_METHOD_NO_USER("Dao method %s.%s does not conform to Access requirement (User as first argument)"), // declaringClassName, methodName
	DAO_METHOD_NO_ENTITY("Dao method %s.%s does not conform to Access requirement (No entity in arguments)"), // declaringClassName, methodName
	DAO_METHOD_NO_ORGSCOPEDENTITY("Dao method %s.%s does not conform to Accessrequirement (Entity is not of type OrgScopedEntity but %s)"), // declaringClass, methodName, entity
	DAO_METHOD_ENTITY_ORGID_NULL("Dao method %s.%s entity does not have an orgId (entity: %s)"); // declaringClass, methodName, entity

	private final String message;

	AccessError(String message) {
		this.message = message;
	}


	@Override
	public String getMessage() {
		return message;
	}
}
