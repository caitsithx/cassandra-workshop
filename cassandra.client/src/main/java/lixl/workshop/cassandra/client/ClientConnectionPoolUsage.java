/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client;

import lixl.workshop.cassandra.client.da.DaoException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public class ClientConnectionPoolUsage {
	private Log LOGGER = LogFactory.getLog(ClientConnectionPoolUsage.class);
	
	/**
	 * @throws DaoException
	 */
	public final void useClientConnection(ClientConnectionAction p_action) throws DaoException {

		ClientConnection l_clientConn = null;
		try {
			try {
				l_clientConn = clientConnPool.borrowObject(); 
			} catch (Exception ex1) {
				throw new DaoException("fail to aquire client conn!", ex1);
			}
			p_action.doWhat(l_clientConn);
		} finally {
			if(l_clientConn != null) {
				try {
					clientConnPool.returnObject(l_clientConn);
				} catch (Exception ex) {
					LOGGER.error("failed to return client conn to pool!", ex);
				}
			}
		}
	}

	private ClientConnectionPool clientConnPool;
	/**
	 * @param p_clientConnectionPool
	 */
	public void setConnectionPool(ClientConnectionPool p_clientConnectionPool) {
		this.clientConnPool = p_clientConnectionPool;
	}
}
