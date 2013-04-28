/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client;

import lixl.workshop.cassandra.client.da.DaoException;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public interface ClientConnectionAction {

	/**
	 * @param p_clientConn
	 * @throws DaoException
	 */
	void doWhat(ClientConnection p_clientConn) throws DaoException;
}
