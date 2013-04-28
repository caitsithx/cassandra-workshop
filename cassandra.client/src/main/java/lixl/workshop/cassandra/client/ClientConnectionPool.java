/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
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
