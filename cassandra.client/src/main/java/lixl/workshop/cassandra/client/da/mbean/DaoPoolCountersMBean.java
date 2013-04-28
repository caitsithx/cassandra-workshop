/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.da.mbean;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public interface DaoPoolCountersMBean {
	String OBJECT_NAME = "lixl.workshop.cassandra:name=daopool";

	int getNumActive();
	int getNumIdle();
	int getMaxActive();
}
