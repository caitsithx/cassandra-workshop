/*
 * Copyright (c) 2013 - caitsithx@live.cn.
 *
 */
package lixl.workshop.cassandra.connection.jmx;

/**
 * @author <a href="mailto:caitsithx@live.cn">lixl </a>
 *
 */
public interface ConnectionPoolCountersMBean {
	String OBJECT_NAME = "lixl.workshop.cassandra:name=daopool";

	int getNumActive();
	int getNumIdle();
	int getMaxActive();
}
