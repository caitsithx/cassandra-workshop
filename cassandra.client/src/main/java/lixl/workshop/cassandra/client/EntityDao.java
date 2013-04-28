/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lixl.workshop.cassandra.client.da.DaoException;
import lixl.workshop.cassandra.client.util.CassandraDataEncoder;
import lixl.workshop.cassandra.model.CassandraType;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;


/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public class EntityDao extends DaoBase{
	private Log LOGGER = LogFactory.getLog(EntityDao.class);

	/**
	 * @param type
	 * @param key
	 * @return found entity or null
	 */
	public <T> T find(Class<T> type, Object key) {
		return null;
	}
	

	/**
	 * @param p_entities
	 *            entity to insert into nsql DB
	 * @throws DaoException 
	 */
	public <T> void insert(final Object... p_entities) throws DaoException {
		Class<?> entityClass = p_entities.getClass();
		
		Map<ByteBuffer, Map<String, List<Mutation>>> l_entityMap = null;
		for (Object l_entity : p_entities) {
			if(l_entity == null) {
				//TODO
				continue;
			}
			
			ColumnFamily_A cfAnnotation = entityClass.getAnnotation(ColumnFamily_A.class);
			if (cfAnnotation != null) {
				
				try {
					if (cfAnnotation.isSuper()) {
						l_entityMap = resolveSuperColumnFamily(l_entity);
					} else {
						l_entityMap = resolveColumnFamily(l_entity);
					}
				} catch (IllegalArgumentException | IllegalAccessException ex) {
					throw new DaoException("resolve (super) column family err", ex);
				}
			}			
		}
		
		BatchMutateAction l_batchMutation = new BatchMutateAction(l_entityMap, ConsistencyLevel.ALL);
		
		access(l_batchMutation);
	}

	/**
	 * transform an <SF> entity to a cassandra-formatted structure
	 * 
	 * @param entity
	 *            entity instance
	 * @return cassandra-formatted structure ready to batch_insert()
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	protected <T> Map<ByteBuffer, Map<String, List<Mutation>>> resolveColumnFamily(
			T entity) throws IllegalArgumentException, IllegalAccessException {
		byte[] key = null;
		Field[] keyAndColumnFlds = entity.getClass().getDeclaredFields();

		ColumnFamily_A cfAnnotation = entity.getClass().getAnnotation(
				ColumnFamily_A.class);
		String cfName = cfAnnotation.name().isEmpty() ? entity.getClass()
				.getSimpleName() : cfAnnotation.name();

		ArrayList<Mutation> l_mutList = new ArrayList<Mutation>();
		for (Field l_field : keyAndColumnFlds) {
			l_field.setAccessible(true);
			if (l_field.isAnnotationPresent(Key_A.class)) {
				String keyStr = (String) l_field.get(entity);
				key = Charset.forName("UTF-8").encode(keyStr).array();
			} else if (l_field.isAnnotationPresent(Column_A.class)) {
				Column_A columnA = l_field.getAnnotation(Column_A.class);

				// get column name
				String columnName = columnA.name().isEmpty() ? l_field
						.getName() : columnA.name();

				Mutation l_columnMut = buildMut(columnName,
						l_field.get(entity), columnA.type());

				l_mutList.add(l_columnMut);
			}
		}

		HashMap<String, List<Mutation>> l_columnMap = new HashMap<String, List<Mutation>>();
		l_columnMap.put(cfName, l_mutList);

		Map<ByteBuffer, Map<String, List<Mutation>>> l_cfMap = new HashMap<ByteBuffer, Map<String, List<Mutation>>>();
		l_cfMap.put(ByteBuffer.wrap(key), l_columnMap);

		return l_cfMap;
	}

	protected <T> Map<ByteBuffer, Map<String, List<Mutation>>> resolveSuperColumnFamily(T entity)
			throws IllegalArgumentException, IllegalAccessException {
		byte[] key = null;
		Field[] keyAndSpColumnFlds = entity.getClass().getDeclaredFields();

		ColumnFamily_A cfAnnotation = entity.getClass().getAnnotation(
				ColumnFamily_A.class);
		String cfName = cfAnnotation.name().isEmpty() ? entity.getClass()
				.getSimpleName() : cfAnnotation.name();

		ArrayList<Mutation> l_mutList = new ArrayList<Mutation>();
		for (Field l_field : keyAndSpColumnFlds) {
			l_field.setAccessible(true);
			if (l_field.isAnnotationPresent(Key_A.class)) {
				String keyStr = (String) l_field.get(entity);
				key = Charset.forName("UTF-8").encode(keyStr).array();
			} else {
				Class<?> fieldClass = l_field.getType();

				SuperColumn_A scAnnotation = fieldClass.getAnnotation(SuperColumn_A.class);

				if (scAnnotation == null) {
					continue;
				}

				Mutation l_mut = getSuperColumn(l_field.get(entity));

				l_mutList.add(l_mut);
			}
		}
		
		HashMap<String, List<Mutation>> l_columnMap = new HashMap<String, List<Mutation>>();
		l_columnMap.put(cfName, l_mutList);
		
		Map<ByteBuffer, Map<String, List<Mutation>>> l_cfMap = new HashMap<ByteBuffer, Map<String, List<Mutation>>>();
		l_cfMap.put(ByteBuffer.wrap(key), l_columnMap);
		
		return l_cfMap;
	}

	/**
	 * @param p_object
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	protected Mutation getSuperColumn(Object p_object)
			throws IllegalArgumentException, IllegalAccessException {
//		SuperColumn_A scAnnotation = p_object.getClass().getAnnotation(
//				SuperColumn_A.class);
		SuperColumn l_spColumn = new SuperColumn();
		ArrayList<Column> l_columnList = new ArrayList<Column>();
		l_spColumn.setColumns(l_columnList);

		Field[] l_scFields = p_object.getClass().getDeclaredFields();
		for (Field field : l_scFields) {
			field.setAccessible(true);

			if (field.isAnnotationPresent(Key_A.class)) {
				l_spColumn.setName(Charset.forName("UTF-8").encode(
						(String) field.get(p_object)));
			} else if (field.isAnnotationPresent(Column_A.class)) {
				Column_A columnA = field.getAnnotation(Column_A.class);

				// get column name
				String columnName = columnA.name().isEmpty() ? field.getName()
						: columnA.name();
				Column l_column = buildColumn(columnName, field.get(p_object),
						columnA.type());
				l_columnList.add(l_column);
			}
		}
		ColumnOrSuperColumn spColumnWrapper = new ColumnOrSuperColumn();
		spColumnWrapper.setSuper_column(l_spColumn);

		Mutation l_mut = new Mutation();
		l_mut.setColumn_or_supercolumn(spColumnWrapper);

		return l_mut;
	}

	/**
	 * @param p_columnName
	 * @param p_object
	 * @param p_type
	 * @return
	 */
	private Column buildColumn(String columnName, Object object,
			CassandraType type) {
		Column l_column = new Column(Charset.forName("UTF-8")
				.encode(columnName));
		l_column.setValue(CassandraDataEncoder.encode(object, type));
		return l_column;
	}

	/**
	 * @param p_columnName
	 * @param object
	 * @return
	 */
	protected Mutation buildMut(String columnName, Object object,
			CassandraType type) {
		// build CA column
		Column l_column = buildColumn(columnName, object, type);

		ColumnOrSuperColumn columnWrapper = new ColumnOrSuperColumn();
		columnWrapper.setColumn(l_column);

		Mutation l_mut = new Mutation();
		l_mut.setColumn_or_supercolumn(columnWrapper);

		return l_mut;
	}

}
