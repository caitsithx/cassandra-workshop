/*
 * Copyright (c) 2013 - caitsithx@live.cn.
 *
 */
package lixl.workshop.cassandra.client.jpalike;

import lixl.workshop.cassandra.client.jpalike.ColumnFamily_A;
import lixl.workshop.cassandra.client.jpalike.Column_A;
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
