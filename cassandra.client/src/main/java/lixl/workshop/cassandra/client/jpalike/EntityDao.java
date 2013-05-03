/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.jpalike;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lixl.workshop.cassandra.client.DaoBase;
import lixl.workshop.cassandra.client.DaoException;
import lixl.workshop.cassandra.model.CassandraType;
import lixl.workshop.cassandra.util.CassandraDataEncoder;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public class EntityDao extends DaoBase{
	private Log LOGGER = LogFactory.getLog(EntityDao.class);

	private EntityContext entityContext;
	/**
	 * @param type
	 * @param key
	 * @return found entity or null
	 * @throws DaoException 
	 */
	public <T> T find(Class<T> type, Object key) throws DaoException {
		SCFDescriptor l_scfDesc = entityContext.findSCFDescriptor(type);
		
		ColumnParent l_colParent = new ColumnParent(l_scfDesc.getName());

		SlicePredicate l_predicate = new SlicePredicate();
		SliceRange l_range = new SliceRange();
		byte[] l_rangeVal = new byte[0];
		l_range.setStart(l_rangeVal);
		l_range.setFinish(l_rangeVal);
		l_predicate.setSlice_range(l_range);

		List<ColumnOrSuperColumn> l_columns = null;
		//		try {
		//			l_columns = getClient().get_slice(ByteBuffer.wrap(p_key),
		//					m_columnFam, l_predicate, ConsistencyLevel.ONE);
		//		} catch (InvalidRequestException | UnavailableException
		//				| TimedOutException | TException ex) {
		//			LOGGER.error("find by key error", ex);
		//			throw new DaoException("find by key error", ex);
		//		}
		String keyStr = (String) key;
		byte[] l_keyBytes = Charset.forName("UTF-8").encode(keyStr).array();
		GetSliceAction l_getSlice = new GetSliceAction(ByteBuffer.wrap(l_keyBytes), 
				ConsistencyLevel.ONE, l_predicate, l_colParent);

		access(l_getSlice);

		l_columns = l_getSlice.getResultList();
		
//		if(l_scfDesc.isSuper()) {
			findSuperColumnFamily(l_scfDesc, l_columns);
//		} else {
//			findColumnFamily(type, key, l_columns);
//		}

		return null;
	}

	/**
	 * @param p_scfDesc
	 * @param p_columns
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws IllegalArgumentException 
	 */
	private Object findSuperColumnFamily(SCFDescriptor p_scfDesc, Object p_key,
			List<ColumnOrSuperColumn> p_columns) throws InstantiationException, 
			IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		Object l_entity = p_scfDesc.getKlass().newInstance();
		
		p_scfDesc.getKlass().getField(p_scfDesc.keyDescriptor.getFieldName()).set(l_entity, p_key);
		
		for (ColumnOrSuperColumn l_superColWrapper : p_columns) {
			SuperColumn l_superCol = l_superColWrapper.getSuper_column();
			l_superCol.getColumns();
		}
		
		return l_entity;
	}

	protected <T> T findSuperColumnFamily(Class<T> p_entityClass, Object p_key, 
			List<ColumnOrSuperColumn> p_superCols) 
					throws InstantiationException, IllegalAccessException {
		T l_entity = p_entityClass.newInstance();
		
		Field[] l_keyAndColumnFlds = p_entityClass.getDeclaredFields();

		ColumnFamily_A cfAnnotation = p_entityClass.getAnnotation(
				ColumnFamily_A.class);
		String l_cfName = cfAnnotation.name().isEmpty() ? p_entityClass
				.getSimpleName() : cfAnnotation.name();

				byte[] l_keyBytes = null;
				for (Field l_field : l_keyAndColumnFlds) {
					l_field.setAccessible(true);
					if (l_field.isAnnotationPresent(Key_A.class)) {
						l_field.set(l_entity, p_key);
					} else {
						Class<?> fieldClass = l_field.getType();

						SuperColumn_A scAnnotation = fieldClass.getAnnotation(SuperColumn_A.class);

						if (scAnnotation == null) {
							continue;
						}
						
						Object l_superColIns = fieldClass.newInstance();
						

					}
				}

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
