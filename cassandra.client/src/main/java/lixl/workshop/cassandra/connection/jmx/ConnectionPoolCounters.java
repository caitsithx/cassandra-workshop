/*
 * Copyright (c) 2013 - Xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.connection.jmx;

import lixl.workshop.cassandra.connection.ClientConnectionPool;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">LI Xiaoliang </a>
 *
 */
public class ConnectionPoolCounters implements ConnectionPoolCountersMBean{

	private ClientConnectionPool connPool;
	
	/**
	 * @param p_connPool the connPool to set
	 */
	public void setConnPool(ClientConnectionPool p_connPool) {
		this.connPool = p_connPool;
	}

	@Override
	public int getNumActive() {
		return connPool.getNumActive();
	}

	@Override
	public int getNumIdle() {
		return connPool.getNumIdle();
	}

	@Override
	public int getMaxActive() {
		return connPool.getMaxActive();
	}

}
