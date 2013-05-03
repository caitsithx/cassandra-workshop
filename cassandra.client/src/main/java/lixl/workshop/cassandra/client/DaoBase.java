/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import lixl.workshop.cassandra.connection.ClientConnection;
import lixl.workshop.cassandra.connection.ClientConnectionAction;
import lixl.workshop.cassandra.connection.ClientConnectionPool;
import lixl.workshop.cassandra.connection.ClientConnectionPoolUsage;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.TException;


/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public class DaoBase {
	private Log LOGGER = LogFactory.getLog(DaoBase.class);
	
	
	private ClientConnectionPool clientConnectionPool;
	
	/**
	 * @return the clientConnectionPool
	 */
	public ClientConnectionPool getClientConnectionPool() {
		return this.clientConnectionPool;
	}

	/**
	 * @param p_clientConnectionPool the clientConnectionPool to set
	 */
	public void setClientConnectionPool(ClientConnectionPool p_clientConnectionPool) {
		this.clientConnectionPool = p_clientConnectionPool;
	}
	
	protected void access(ClientConnectionAction p_action) throws DaoException {
		ClientConnectionPoolUsage l_connPoolUsage = new ClientConnectionPoolUsage();
		l_connPoolUsage.setConnectionPool(this.clientConnectionPool);
		
		l_connPoolUsage.useClientConnection(p_action);
	}
	
	public class BatchMutateAction implements ClientConnectionAction {
		private Map<ByteBuffer, Map<String, List<Mutation>>> data;
		private ConsistencyLevel consistLevel;
		
		public BatchMutateAction(Map<ByteBuffer, Map<String, List<Mutation>>> p_data,
				ConsistencyLevel p_consistLevel) {
			data = p_data;
			consistLevel = p_consistLevel;
		}
		
		
		@Override
		public void doWhat(ClientConnection p_clientConn) throws DaoException {
			try {
				p_clientConn.getClient().batch_mutate(data, consistLevel);
			} catch (InvalidRequestException | UnavailableException
					| TimedOutException | TException ex) {
				throw new DaoException("batch insertion err!", ex);
			}				
		}
	}
	
	public class InsertAction implements ClientConnectionAction {
		/**
		 * @param p_key
		 * @param p_consistLevel
		 * @param p_columnParent
		 * @param p_column
		 */
		public InsertAction(ByteBuffer p_key, ColumnParent p_columnParent, 
				Column p_column, ConsistencyLevel p_consistLevel) {
			super();
			this.key = p_key;
			this.consistLevel = p_consistLevel;
			this.columnParent = p_columnParent;
			this.column = p_column;
		}

		private ByteBuffer key;
		private ConsistencyLevel consistLevel;
		private ColumnParent columnParent;
		private Column column;

		/* (non-Javadoc)
		 * @see me.lixl.nosql.persistence.ClientConnectionAction#doWhat(me.lixl.nosql.persistence.ClientConnection)
		 */
		@Override
		public void doWhat(ClientConnection p_clientConn) throws DaoException {
			try {
				p_clientConn.getClient().insert(key, columnParent, column,
						consistLevel);
			} catch (Exception ex)
			/*
			 * catch (InvalidRequestException | UnavailableException |
			 * TimedOutException | TException ex)
			 */{
				LOGGER.error("insert err", ex);
				throw new DaoException("insert err", ex);
			}			
		}
		
	}
	
	public class GetSliceAction implements ClientConnectionAction {
		/**
		 * @param p_key
		 * @param p_consistLevel
		 * @param p_predicate
		 * @param p_columnParent
		 */
		public GetSliceAction(ByteBuffer p_key,
				ConsistencyLevel p_consistLevel, SlicePredicate p_predicate,
				ColumnParent p_columnParent) {
			this.key = p_key;
			this.consistLevel = p_consistLevel;
			this.predicate = p_predicate;
			this.columnParent = p_columnParent;
		}

		private ByteBuffer key;
		private ConsistencyLevel consistLevel;
		private SlicePredicate predicate = null;
		private ColumnParent columnParent;
		private List<ColumnOrSuperColumn> resultList;
		
		/**
		 * @return the resultList
		 */
		public List<ColumnOrSuperColumn> getResultList() {
			return this.resultList;
		}

		/* (non-Javadoc)
		 * @see me.lixl.nosql.persistence.ClientConnectionAction#doWhat(me.lixl.nosql.persistence.ClientConnection)
		 */
		@Override
		public void doWhat(ClientConnection p_clientConn) throws DaoException {
			try {
				resultList = p_clientConn.getClient().get_slice(key,
						columnParent, predicate, consistLevel);
			} catch (InvalidRequestException | UnavailableException
					| TimedOutException | TException ex) {
				LOGGER.error("find by key error", ex);
				throw new DaoException("find by key error", ex);
			}			
		}
		
	}
}