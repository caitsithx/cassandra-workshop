/*
 * Copyright (c) 2013 - caitsithx@live.cn.
 *
 */
package lixl.workshop.cassandra.app.runner;

import lixl.concurrent.runner.thread.TaskRunnable;
import lixl.workshop.cassandra.client.simple.EncodedVO;
import lixl.workshop.cassandra.client.simple.SimpleDao;

import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:caitsithx@live.cn">lixl </a>
 *
 */
public class UserInsertionRunnable extends TaskRunnable{
	private final static Logger LOGGER = Logger.getLogger(UserInsertionRunnable.class);
//	private ObjectPool<SimpleDao> m_daoPool = null;
	
	private SimpleDao simpleDao = null;
	private EncodedVO encodedValueObject;


	/**
	 * @param p_simpleDao the simpleDao to set
	 */
	public void setSimpleDao(SimpleDao p_simpleDao) {
		this.simpleDao = p_simpleDao;
	}
	
	/**
	 * @param p_vo the vo to set
	 */
	public void setValueObject(EncodedVO p_vo) {
		this.encodedValueObject = p_vo;
	}

//	/**
//	 * @param p_daoPool the daoPool to set
//	 */
//	public void setDaoPool(GenericObjectPool<SimpleDao> p_daoPool) {
//		this.m_daoPool = p_daoPool;
//	}

	@Override
	protected int doTask() {

		try {
			simpleDao.insert(encodedValueObject);
		} catch (Exception ex) {
			LOGGER.error("fail runnable exec", ex);
		} 
		
		return 1;
	}

}
