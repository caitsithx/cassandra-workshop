/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.da.mbean;

import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public class DaoPoolCounters implements DaoPoolCountersMBean{

	private GenericObjectPool<?> m_daoPool;

	/**
	 * @param p_daoPool the daoPool to set
	 */
	public void setDaoPool(GenericObjectPool<?> p_daoPool) {
		this.m_daoPool = p_daoPool;
	}

	public int getNumActive() {
		return m_daoPool.getNumActive();
	}

	public int getNumIdle() {
		return m_daoPool.getNumIdle();
	}

	public int getMaxActive() {
		return m_daoPool.getMaxActive();
	}

	
}
