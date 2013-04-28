/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client;

import lixl.workshop.cassandra.client.ColumnFamily_A;
import lixl.workshop.cassandra.client.Column_A;
import lixl.workshop.cassandra.model.CassandraType;


public class TestColumnFamily {
	
	@ColumnFamily_A(name="MyCF")
	class MyColumnFamily {
		
		@Column_A(name="name", type=CassandraType.TEXT)
		private String name;
		
		@Column_A(name="age", type=CassandraType.INT)
		private int age;
		
	}
	

}
