package org.projectmvc.access.annotation;

import org.projectmvc.access.OrgPrivilege;
import org.projectmvc.access.SystemPrivilege;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is the annotation to add in the Web REST methods to add System level privileges.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AssertWebSystemPrivileges {

	SystemPrivilege[] value() default {};

}
