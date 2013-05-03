/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.simple;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lixl.workshop.cassandra.client.DaoBase;
import lixl.workshop.cassandra.client.DaoException;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public class SimpleDao extends DaoBase{
	private final static Log LOGGER = LogFactory.getLog(SimpleDao.class);

	private String m_columnFamily = "employee";
	private ColumnParent m_columnFam = new ColumnParent(m_columnFamily);
	
	// public SimpleDao () {
	// TTransport tr = new TFramedTransport(new TSocket(m_host, m_port));
	// TProtocol proto = new TBinaryProtocol(tr);
	// Cassandra.Client client = new Cassandra.Client(proto);
	// tr.open();
	//
	//
	// // insert data
	//
	// client.set_keyspace(m_keySpace);
	// ColumnParent parent = new ColumnParent(m_columnFamily);
	// }

	/**
	 * @param p_columnFamily
	 *            the columnFamily to set
	 */
	public void setColumnFamily(String p_columnFamily) {
		this.m_columnFamily = p_columnFamily;
		m_columnFam = new ColumnParent(m_columnFamily);
	}

	public void insert(EncodedVO p_vo) throws DaoException {
		if (p_vo == null || p_vo.getKey() == null && p_vo.getKey().length == 0) {
			throw new DaoException("insert null/empty key");
		}

		ByteBuffer l_keyBuf = ByteBuffer.wrap(p_vo.getKey());

		if (p_vo.getValues() == null || p_vo.getValues().isEmpty()) {
			throw new DaoException("insert null/empty column");
		}

		for (Entry<byte[], byte[]> l_colEntry : p_vo.getValues().entrySet()) {
			long timestamp = System.currentTimeMillis();
			checkColumn(l_colEntry.getKey(), l_colEntry.getValue());

			Column ageColumn = new Column(ByteBuffer.wrap(l_colEntry.getKey()));
			ageColumn.setValue(l_colEntry.getValue());
			ageColumn.setTimestamp(timestamp);

//			try {
//				getClient().insert(l_keyBuf, m_columnFam, ageColumn,
//						ConsistencyLevel.ONE);
//			} catch (Exception ex)
//			/*
//			 * catch (InvalidRequestException | UnavailableException |
//			 * TimedOutException | TException ex)
//			 */{
//				LOGGER.error("insert err", ex);
//				throw new DaoException("insert err", ex);
//			}
			
			InsertAction l_insert = new InsertAction(l_keyBuf, m_columnFam, ageColumn, 
					ConsistencyLevel.ANY);
			
			access(l_insert);
		}

	}

	public void insertintoSuperColumnFamily() throws DaoException {

	}

	/**
	 * insert by batch
	 * 
	 * @param p_vo
	 *            one VO or list of VOs
	 * @throws DaoException
	 */
	public void inserttoColumnFamily(EncodedVO... p_vo) throws DaoException {

		Map<ByteBuffer, Map<String, List<Mutation>>> l_batchInsert = new HashMap<>();

		for (EncodedVO l_encodedVO : p_vo) {
			l_batchInsert.put(ByteBuffer.wrap(l_encodedVO.getKey()),
					toColumnFamilyMuts(l_encodedVO));
		}

//		try {
//			getClient().batch_mutate(l_batchInsert, ConsistencyLevel.ANY);
//		} catch (Exception ex)
//		/*
//		 * catch (InvalidRequestException | UnavailableException |
//		 * TimedOutException | TException ex)
//		 */{
//			LOGGER.error("insert err", ex);
//			throw new DaoException("insert err", ex);
//		}
		
		BatchMutateAction l_batchMutation = new BatchMutateAction(l_batchInsert, 
				ConsistencyLevel.ANY);
		
		access(l_batchMutation);
	}

	protected Map<String, List<Mutation>> toColumnFamilyMuts(EncodedVO p_vo)
			throws DaoException {
		if (p_vo == null || p_vo.getKey() == null && p_vo.getKey().length == 0) {
			throw new DaoException("insert null/empty key");
		}

		if (p_vo.getValues() == null || p_vo.getValues().isEmpty()) {
			throw new DaoException("insert null/empty column");
		}

		List<Mutation> l_mutList = new ArrayList<>();
		for (Entry<byte[], byte[]> l_colEntry : p_vo.getValues().entrySet()) {
			long timestamp = System.currentTimeMillis();
			checkColumn(l_colEntry.getKey(), l_colEntry.getValue());

			Column l_column = new Column(ByteBuffer.wrap(l_colEntry.getKey()));
			l_column.setValue(l_colEntry.getValue());
			l_column.setTimestamp(timestamp);

			ColumnOrSuperColumn l_supColumnWrapper = new ColumnOrSuperColumn();
			l_supColumnWrapper.setColumn(l_column);
			Mutation l_supColumnInsert = new Mutation();
			l_supColumnInsert.setColumn_or_supercolumn(l_supColumnWrapper);
			l_mutList.add(l_supColumnInsert);
		}

		Map<String, List<Mutation>> l_muts = new HashMap<>();
		l_muts.put(m_columnFamily, l_mutList);

		return l_muts;
	}

	/**
	 * @param p_key
	 * @param p_value
	 * @throws DaoException
	 */
	protected static void checkColumn(byte[] p_key, byte[] p_value)
			throws DaoException {
		if (p_key == null || p_key.length == 0) {
			throw new DaoException("empty column id!");
		}

		if (p_value == null || p_value.length == 0) {
			throw new DaoException("empty column value!");
		}
	}

	public EncodedVO find(byte[] p_key) throws DaoException {
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
		
		GetSliceAction l_getSlice = new GetSliceAction(ByteBuffer.wrap(p_key), 
				ConsistencyLevel.ONE, l_predicate, m_columnFam);
		
		access(l_getSlice);
		
		l_columns = l_getSlice.getResultList();
		
		EncodedVO l_vo = null;
		if (l_columns != null) {
			l_vo = new EncodedVO();
			l_vo.setKey(p_key);
			for (ColumnOrSuperColumn columnOrSuperColumn : l_columns) {
				Column l_column = columnOrSuperColumn.getColumn();
				l_vo.addColumn(l_column.getName(), l_column.getValue());
			}
		}

		return l_vo;
	}
}
