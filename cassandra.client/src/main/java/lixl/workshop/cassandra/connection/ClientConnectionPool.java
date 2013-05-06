/*
 * Copyright (c) 2013 - caitsithx@live.cn.
 *
 */
package lixl.workshop.cassandra.connection;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * @author <a href="mailto:caitsithx@live.cn">lixl </a>
 *
 */
public class ClientConnectionPool extends GenericObjectPool<ClientConnection>{

	/**
	 * @param p_factory
	 */
	public ClientConnectionPool(
			PoolableObjectFactory<ClientConnection> p_factory) {
		super(p_factory);
		// TODO Auto-generated constructor stub
	}

}
