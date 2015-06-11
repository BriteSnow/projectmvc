package org.projectmvc.access.annotation;

import org.projectmvc.access.OrgPrivilege;
import org.projectmvc.access.ProjectPrivilege;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is the annotation to add in the Dao methods (for now, later, we will add then to @Web... as well).
 *
 * It will get used by ...AccessInterceptor to know which privilege to assert.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AssertParamProjectPrivileges {

	ProjectPrivilege[] value() default {};

}
