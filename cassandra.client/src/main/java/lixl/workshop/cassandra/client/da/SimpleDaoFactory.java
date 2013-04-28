/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.da;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.pool.PoolableObjectFactory;


/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
@Deprecated
public class SimpleDaoFactory implements PoolableObjectFactory<SimpleDao> {
	private Configuration m_config;
	
	public SimpleDaoFactory(Configuration p_cfg) {
		m_config = p_cfg;
	}

	public SimpleDao makeObject() throws Exception {
		SimpleDao l_dao = new SimpleDao();
		
//		l_dao.setHost(m_config.getString("host"));
//		l_dao.setPort(m_config.getInt("port"));
//		l_dao.setKeySpace(m_config.getString("keyspace"));
//		l_dao.setColumnFamily(m_config.getString("columnfamily"));
		
		return l_dao;
	}

	public void activateObject(SimpleDao p_arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void destroyObject(SimpleDao p_arg0) throws Exception {
//		p_arg0.closeConnection();
	}

	public void passivateObject(SimpleDao p_arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public boolean validateObject(SimpleDao p_arg0) {
		return true;
	}
	
}
