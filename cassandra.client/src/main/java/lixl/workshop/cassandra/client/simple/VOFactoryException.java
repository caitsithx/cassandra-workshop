/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.simple;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public class VOFactoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5025839811411587794L;

	/**
	 * 
	 */
	public VOFactoryException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param p_message
	 */
	public VOFactoryException(String p_message) {
		super(p_message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param p_cause
	 */
	public VOFactoryException(Throwable p_cause) {
		super(p_cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param p_message
	 * @param p_cause
	 */
	public VOFactoryException(String p_message, Throwable p_cause) {
		super(p_message, p_cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param p_message
	 * @param p_cause
	 * @param p_enableSuppression
	 * @param p_writableStackTrace
	 */
	public VOFactoryException(String p_message, Throwable p_cause,
			boolean p_enableSuppression, boolean p_writableStackTrace) {
		super(p_message, p_cause, p_enableSuppression, p_writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
