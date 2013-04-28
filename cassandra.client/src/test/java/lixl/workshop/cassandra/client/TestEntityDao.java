/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import lixl.workshop.cassandra.client.ColumnFamily_A;
import lixl.workshop.cassandra.client.Column_A;
import lixl.workshop.cassandra.client.EntityDao;
import lixl.workshop.cassandra.client.Key_A;
import lixl.workshop.cassandra.client.SuperColumn_A;
import lixl.workshop.cassandra.model.CassandraType;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SuperColumn;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import tools.utilities.general.ByteArrayUtilities;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">LI Xiaoliang </a>
 * 
 */
public class TestEntityDao {
	@ColumnFamily_A(name = "MyCF")
	class MyColumnFamily {

		@Key_A
		private String key;

		@Column_A(name = "name", type = CassandraType.TEXT)
		private String name;

		@Column_A(name = "age", type = CassandraType.INT)
		private int age;

	}

	@ColumnFamily_A(name = "MySCF")
	class MySuperColumnFamily {
		@Key_A
		private String key;

		private MySuperColumn mySupCol;
	}

	@SuperColumn_A
	class MySuperColumn {
		@Key_A
		private String key;

		@Column_A(name = "name", type = CassandraType.TEXT)
		private String name;

		@Column_A(name = "age", type = CassandraType.INT)
		private int age;
	}

	@Test
	public void testResolveColumnFamily() throws IllegalArgumentException,
			IllegalAccessException {
		MyColumnFamily l_cf = new MyColumnFamily();
		l_cf.key = "lixlkey";
		l_cf.age = 10;
		l_cf.name = "lixl";

		EntityDao l_dao = new EntityDao();

		Map<ByteBuffer, Map<String, List<Mutation>>> l_mutStruct = l_dao
				.resolveColumnFamily(l_cf);

		Assert.assertEquals(1, l_mutStruct.size());

		ByteBuffer l_keyBuffer = Charset.forName("UTF-8").encode("lixlkey");

		Map<String, List<Mutation>> l_mutSubStruct = l_mutStruct
				.get(l_keyBuffer);

		Assert.assertEquals(1, l_mutSubStruct.size());

		List<Mutation> l_mutList = l_mutSubStruct.get("MyCF");

		Assert.assertEquals(2, l_mutList.size());

		Column l_nameCol = new Column(Charset.forName("UTF-8").encode("name"));
		l_nameCol.setValue(Charset.forName("UTF-8").encode("lixl"));
		ColumnOrSuperColumn l_wrapper = new ColumnOrSuperColumn();
		l_wrapper.setColumn(l_nameCol);
		Mutation l_mut = new Mutation();
		l_mut.setColumn_or_supercolumn(l_wrapper);

		Column l_ageCol = new Column(Charset.forName("UTF-8").encode("age"));
		l_ageCol.setValue(ByteArrayUtilities.intToByteArray(10));
		l_wrapper = new ColumnOrSuperColumn();
		l_wrapper.setColumn(l_ageCol);
		Mutation l_mut1 = new Mutation();
		l_mut1.setColumn_or_supercolumn(l_wrapper);

		Assert.assertThat(l_mutList, CoreMatchers.hasItems(l_mut, l_mut1));

	}

	@Test
	public void testResolveSuperColumnFamily() throws IllegalArgumentException,
			IllegalAccessException {
		MySuperColumn l_mySupCol = new MySuperColumn();
		l_mySupCol.age = 30;
		l_mySupCol.name = "lixl";
		l_mySupCol.key = "archicell";

		MySuperColumnFamily l_mySupCF = new MySuperColumnFamily();
		l_mySupCF.key = "beijing";
		l_mySupCF.mySupCol = l_mySupCol;

		EntityDao l_dao = new EntityDao();

		Map<ByteBuffer, Map<String, List<Mutation>>> l_mutStruct = l_dao
				.resolveSuperColumnFamily(l_mySupCF);

		Assert.assertEquals(1, l_mutStruct.size());
		
		ByteBuffer l_keyBuffer = Charset.forName("UTF-8").encode("beijing");
		Map<String, List<Mutation>> l_mutSubStruct = l_mutStruct
				.get(l_keyBuffer);
		
		Assert.assertEquals(1, l_mutSubStruct.size());
		
		List<Mutation> l_mutList = l_mutSubStruct.get("MySCF");
		
		Assert.assertEquals(1, l_mutList.size());
		
		Mutation l_mut = l_mutList.get(0);
		
		SuperColumn l_supCol = l_mut.getColumn_or_supercolumn().getSuper_column();
		
		Assert.assertArrayEquals(Charset.forName("UTF-8").encode("archicell").array(), 
				l_supCol.getName());
		
		
		
		Column l_nameCol = new Column(Charset.forName("UTF-8").encode("name"));
		l_nameCol.setValue(Charset.forName("UTF-8").encode("lixl"));
		Column l_ageCol = new Column(Charset.forName("UTF-8").encode("age"));
		l_ageCol.setValue(ByteArrayUtilities.intToByteArray(30));
		
		
		Assert.assertThat(l_supCol.getColumns(), CoreMatchers.hasItems(l_nameCol, l_ageCol));
	}
}
