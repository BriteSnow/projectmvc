package org.projectmvc.dao;

// IMPORTANT: no Singleton on this one, since one instance per entity will be needed. 
public class GenericDao extends BaseDao {

	public GenericDao(){
		super(true);
	}

	GenericDao setEntityClass(Class entityClass, Class idClass) {
		this.entityClass = entityClass;
		this.idClass = idClass;
		return this;
	}
}
