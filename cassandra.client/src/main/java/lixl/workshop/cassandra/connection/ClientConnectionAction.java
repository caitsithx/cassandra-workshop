/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.connection;

import lixl.workshop.cassandra.client.DaoException;

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
