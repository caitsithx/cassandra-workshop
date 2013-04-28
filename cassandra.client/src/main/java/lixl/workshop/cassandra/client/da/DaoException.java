/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.da;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
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
