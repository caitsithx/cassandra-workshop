/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.da;

import lixl.commons.counters.Counters;
import lixl.commons.jmx.JMXHelper;
import lixl.workshop.cassandra.client.da.mbean.DaoPoolCounters;
import lixl.workshop.cassandra.client.da.mbean.DaoPoolCountersMBean;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;


/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public class SimpleDaoPools {
	private final static Log LOGGER = LogFactory.getLog(SimpleDaoPools.class);

	private GenericObjectPool<SimpleDao> m_pool = null;

	private Configuration m_configuration = null;



	/**
	 * @param p_configuration the configuration to set
	 */
	public void setConfiguration(Configuration p_configuration) {
		this.m_configuration = p_configuration;
	}



	/**
	 * @param p_cfg
	 * @return singleton
	 */
	public final GenericObjectPool<SimpleDao> getSimpleDaoPool() {
		if(m_pool == null) {
			m_pool = new GenericObjectPool<SimpleDao>(new SimpleDaoFactory(m_configuration));
			
			DaoPoolCounters l_counters = new DaoPoolCounters();
			l_counters.setDaoPool(m_pool);
			
			try {
				JMXHelper.getJVMInstance().registerMBean(DaoPoolCountersMBean.OBJECT_NAME, l_counters);
				Counters.getInstance().register(DaoPoolCountersMBean.OBJECT_NAME, l_counters);
			} 
			catch (Exception ex)
			/*catch (JMXHelperException | CountersException ex)*/ {
				LOGGER.error("fail register Mbean!", ex);
			}
		}
		return m_pool;
	}
}
