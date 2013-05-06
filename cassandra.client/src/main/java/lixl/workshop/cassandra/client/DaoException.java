/*
 * Copyright (c) 2013 - caitsithx@live.cn.
 *
 */
package lixl.workshop.cassandra.client;

/**
 * @author <a href="mailto:caitsithx@live.cn">lixl </a>
 *
 */
public class DaoException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1103588559810344972L;

	/**
	 * @param p_message
	 * @param p_cause
	 */
	public DaoException(String p_message, Throwable p_cause) {
		super(p_message, p_cause);
	}

	/**
	 * @param p_message
	 */
	public DaoException(String p_message) {
		super(p_message);
		// TODO Auto-generated constructor stub
	}

}
