/*
 * Copyright (c) 2013 - caitsithx@live.cn.
 *
 */
package lixl.workshop.cassandra.client.jpalike;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import lixl.workshop.cassandra.client.DaoException;
import lixl.workshop.cassandra.model.CassandraType;

/**
 * @author <a href="mailto:caitsithx@live.cn">LI Xiaoliang </a>
 *
 */
public class EntityContext {
	private Map<Class<?>, SCFDescriptor> scfDescriptorCache;
	
	public SCFDescriptor findSCFDescriptor(Class<?> scfClass) {
		return scfDescriptorCache.get(scfClass);
	}
	
	public final static EntityContext getEntityContext(Class<?>... p_entityClasses) throws DaoException {
		EntityContext l_eContext = new EntityContext();
		
		for (Class<?> l_entityClass : p_entityClasses) {
			ColumnFamily_A l_cf_A = l_entityClass.getAnnotation(ColumnFamily_A.class);
			
			if(l_cf_A == null) {
				throw new DaoException("type is not an entity class!");
			}
			
			if(l_cf_A.isSuper()) {
				l_eContext.addSCFDescriptor(l_entityClass);
			}
		}
		
		return l_eContext;
	}

	/**
	 * @param p_entityClass
	 */
	private void addSCFDescriptor(Class<?> p_entityClass) {
		ColumnFamily_A l_cf_A = p_entityClass.getAnnotation(ColumnFamily_A.class);
		
		SCFDescriptor l_scfDesc = new SCFDescriptor();
		l_scfDesc.name = l_cf_A.name().isEmpty() ? p_entityClass.getSimpleName() 
				: l_cf_A.name();
		
		l_scfDesc.setMappingClass(p_entityClass);
		
		l_scfDesc.superColumnMap = new HashMap<>();
		
		Field[] keyAndSpColumnFlds = p_entityClass.getDeclaredFields();
		
		for (Field l_field : keyAndSpColumnFlds) {
			l_field.setAccessible(true);
			if (l_field.isAnnotationPresent(Key_A.class)) {
				//TODO key name and type!!!???
				ColumnDescriptor l_keyDesc = new ColumnDescriptor();
				l_keyDesc.fieldName = l_field.getName();
				l_keyDesc.name = "";
				l_keyDesc.type = CassandraType.TEXT;
			} else {
				Class<?> fieldClass = l_field.getType();

				SuperColumn_A scAnnotation = fieldClass.getAnnotation(SuperColumn_A.class);

				if (scAnnotation == null) {
					continue;
				}

				SCDescriptor l_mut = getSuperColumn(l_field);
				
				l_scfDesc.superColumnMap.put(l_mut.fieldName, l_mut);
			}
		}
	}

	/**
	 * @param p_scField
	 * @return
	 */
	private SCDescriptor getSuperColumn(Field p_scField) {
		SCDescriptor l_scDesc = new SCDescriptor();
		l_scDesc.columnMap = new HashMap<>();
		l_scDesc.fieldName = p_scField.getName();
		l_scDesc.setMappingClass(p_scField.getDeclaringClass());

		Field[] l_cFields = p_scField.getDeclaringClass().getDeclaredFields();
		for (Field l_cField : l_cFields) {
			l_cField.setAccessible(true);

			if (l_cField.isAnnotationPresent(Key_A.class)) {
				l_scDesc.keyFieldName = l_cField.getName();
			} else if (l_cField.isAnnotationPresent(Column_A.class)) {
				Column_A columnA = l_cField.getAnnotation(Column_A.class);

				ColumnDescriptor l_cDesc = new ColumnDescriptor();
				l_cDesc.name = columnA.name().isEmpty() ? l_cField.getName()
						: columnA.name();
				l_cDesc.fieldName = l_cField.getName();
				l_cDesc.type = columnA.type();
				
				l_scDesc.columnMap.put(l_cDesc.name, l_cDesc);
			}
		}
		
		return l_scDesc;
	}
}

class SCFDescriptor {
	/**
	 * @return the mappingClass
	 */
	public Class<?> getKlass() {
		return this.mappingClass;
	}
	/**
	 * @param p_klass the mappingClass to set
	 */
	public void setMappingClass(Class<?> p_klass) {
		this.mappingClass = p_klass;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * @param p_name the name to set
	 */
	public void setName(String p_name) {
		this.name = p_name;
	}
	/**
	 * @return the keyDescriptor
	 */
	public ColumnDescriptor getKeyDescriptor() {
		return this.keyDescriptor;
	}
	/**
	 * @param p_keyDescriptor the keyDescriptor to set
	 */
	public void setKeyDescriptor(ColumnDescriptor p_keyDescriptor) {
		this.keyDescriptor = p_keyDescriptor;
	}
	private Class<?> mappingClass;
	String name;
	
	ColumnDescriptor keyDescriptor;
	Map<String, SCDescriptor> superColumnMap;
	/**
	 * @param p_string
	 * @return
	 */
	public SCDescriptor findSuperColumnDesc(String p_string) {
		return superColumnMap.get(p_string);
	}
	
}

class SCDescriptor {
	Class<?> mappingClass;
	String fieldName;
	String keyFieldName;
	
	Map<String, ColumnDescriptor> columnMap;

	/**
	 * @return the mappingClass
	 */
	public Class<?> getMappingClass() {
		return this.mappingClass;
	}

	/**
	 * @param p_superColumnClass the mappingClass to set
	 */
	public void setMappingClass(Class<?> p_superColumnClass) {
		this.mappingClass = p_superColumnClass;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return this.fieldName;
	}

	/**
	 * @param p_fieldName the fieldName to set
	 */
	public void setFieldName(String p_fieldName) {
		this.fieldName = p_fieldName;
	}

	/**
	 * @return the keyFieldName
	 */
	public String getKeyFieldName() {
		return this.keyFieldName;
	}

	/**
	 * @param p_keyFieldName the keyFieldName to set
	 */
	public void setKeyFieldName(String p_keyFieldName) {
		this.keyFieldName = p_keyFieldName;
	}

	/**
	 * @param p_colName
	 * @return
	 */
	public ColumnDescriptor findColumnDesc(String p_colName) {
		return this.columnMap.get(p_colName);
	}
}

class ColumnDescriptor {
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * @param p_name the name to set
	 */
	public void setName(String p_name) {
		this.name = p_name;
	}
	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return this.fieldName;
	}
	/**
	 * @param p_fieldName the fieldName to set
	 */
	public void setFieldName(String p_fieldName) {
		this.fieldName = p_fieldName;
	}
	/**
	 * @return the type
	 */
	public CassandraType getType() {
		return this.type;
	}
	/**
	 * @param p_type the type to set
	 */
	public void setType(CassandraType p_type) {
		this.type = p_type;
	}
	String name;
	String fieldName;
	CassandraType type;
}
