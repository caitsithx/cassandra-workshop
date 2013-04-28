/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.app.runner;

import lixl.concurrent.runner.thread.TaskRunnable;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

import lixl.workshop.cassandra.client.da.SimpleDao;
import lixl.workshop.cassandra.client.vo.EncodedVO;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public class UserInsertionRunnable extends TaskRunnable{
	private final static Logger LOGGER = Logger.getLogger(UserInsertionRunnable.class);
	private ObjectPool<SimpleDao> m_daoPool = null;
	
	private EncodedVO m_valueObject;

	
	/**
	 * @param p_vo the vo to set
	 */
	public void setValueObject(EncodedVO p_vo) {
		this.m_valueObject = p_vo;
	}

	/**
	 * @param p_daoPool the daoPool to set
	 */
	public void setDaoPool(GenericObjectPool<SimpleDao> p_daoPool) {
		this.m_daoPool = p_daoPool;
	}

	@Override
	protected int doTask() {
		SimpleDao l_dao = null;

		try {
			l_dao = m_daoPool.borrowObject();

			l_dao.insert(m_valueObject);

		} catch (Exception ex) {
			LOGGER.error("fail runnable exec", ex);
		} finally {
			if(l_dao != null){
				try {
					m_daoPool.returnObject(l_dao);
				} catch (Exception ex) {
					LOGGER.error("dao pool return err", ex);
				}
			}
		}
		
		return 1;
	}

}
