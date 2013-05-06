/*
 * Copyright (c) 2013 - caitsithx@live.cn.
 *
 */
package lixl.workshop.cassandra.connection;

import lixl.workshop.cassandra.client.DaoException;

/**
 * @author <a href="mailto:caitsithx@live.cn">lixl </a>
 *
 */
public interface ClientConnectionAction {

	/**
	 * @param p_clientConn
	 * @throws DaoException
	 */
	void doWhat(ClientConnection p_clientConn) throws DaoException;
}
