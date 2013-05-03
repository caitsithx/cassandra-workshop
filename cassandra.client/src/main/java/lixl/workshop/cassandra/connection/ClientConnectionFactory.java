/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.connection;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.pool.PoolableObjectFactory;


/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public class ClientConnectionFactory implements PoolableObjectFactory<ClientConnection>{
	private Configuration configuration;

	/**
	 * @return the configuration
	 */
	public Configuration getConfiguration() {
		return this.configuration;
	}

	/**
	 * @param p_configuration the configuration to set
	 */
	public void setConfiguration(Configuration p_configuration) {
		this.configuration = p_configuration;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
	 */
	@Override
	public ClientConnection makeObject() throws Exception {
		ClientConnection l_clientConn = new ClientConnection();
		
		l_clientConn.setHost(configuration.getString("host"));
		l_clientConn.setPort(configuration.getInt("port"));
		l_clientConn.setKeySpace(configuration.getString("keyspace"));
//		l_dao.setColumnFamily(m_config.getString("columnfamily"));
		
		return l_clientConn;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.PoolableObjectFactory#destroyObject(java.lang.Object)
	 */
	@Override
	public void destroyObject(ClientConnection p_obj) throws Exception {
		p_obj.closeConnection();		
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.PoolableObjectFactory#validateObject(java.lang.Object)
	 */
	@Override
	public boolean validateObject(ClientConnection p_obj) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.PoolableObjectFactory#activateObject(java.lang.Object)
	 */
	@Override
	public void activateObject(ClientConnection p_obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.PoolableObjectFactory#passivateObject(java.lang.Object)
	 */
	@Override
	public void passivateObject(ClientConnection p_obj) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
