/*
 * Copyright (c) 2013 - caitsithx@live.cn.
 *
 */
package lixl.workshop.cassandra.client.jpalike;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import lixl.commons.bytes.ByteArrayUtils;
import lixl.workshop.cassandra.client.DaoException;
import lixl.workshop.cassandra.client.simple.VOFactoryBase;
import lixl.workshop.cassandra.client.simple.VOFactoryException;
import lixl.workshop.cassandra.connection.ClientConnectionFactory;
import lixl.workshop.cassandra.connection.ClientConnectionPool;
import lixl.workshop.cassandra.model.CassandraType;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.Mutation;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:caitsithx@live.cn">LI Xiaoliang </a>
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

	@ColumnFamily_A(name = "MySCF", isSuper=true)
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
	
    private static Configuration CFG = null;
    private static ClientConnectionPool connPool = null;
    
    @BeforeClass
    public static void beforeClass() throws ConfigurationException, VOFactoryException {
        CFG = new PropertiesConfiguration("test.properties"); //$NON-NLS-1$
        VOFactoryBase.loadDefinitionsFromRes("/employee.def.xml");
        
        ClientConnectionFactory l_ccf = new ClientConnectionFactory();
    	l_ccf.setConfiguration(CFG);
    	connPool = new ClientConnectionPool(l_ccf);
    }

	@Test
	public void testResolveColumnFamily() throws IllegalArgumentException,
			IllegalAccessException {
		MyColumnFamily l_cf = new MyColumnFamily();
		l_cf.key = "lixlkey";
		l_cf.age = 10;
		l_cf.name = "lixl";

		EntityDao l_dao = new EntityDao();

		long l_timestamp = System.currentTimeMillis();
		Map<ByteBuffer, Map<String, List<Mutation>>> l_mutStruct = l_dao
				.resolveColumnFamily(l_cf, l_timestamp);

		Assert.assertEquals(1, l_mutStruct.size());

		ByteBuffer l_keyBuffer = Charset.forName("UTF-8").encode("lixlkey");

		Map<String, List<Mutation>> l_mutSubStruct = l_mutStruct
				.get(l_keyBuffer);

		Assert.assertEquals(1, l_mutSubStruct.size());

		List<Mutation> l_mutList = l_mutSubStruct.get("MyCF");

		Assert.assertEquals(2, l_mutList.size());

		Column l_nameCol = new Column(Charset.forName("UTF-8").encode("name"));
		l_nameCol.setValue(Charset.forName("UTF-8").encode("lixl"));
		l_nameCol.setTimestamp(l_timestamp);
		ColumnOrSuperColumn l_wrapper = new ColumnOrSuperColumn();
		l_wrapper.setColumn(l_nameCol);
		Mutation l_mut = new Mutation();
		l_mut.setColumn_or_supercolumn(l_wrapper);

		Column l_ageCol = new Column(Charset.forName("UTF-8").encode("age"));
		l_ageCol.setValue(ByteArrayUtils.unsignedInttoByteArray(10));
		l_ageCol.setTimestamp(l_timestamp);
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

		long l_timestamp = System.currentTimeMillis();
		EntityDao l_dao = new EntityDao();

		Map<ByteBuffer, Map<String, List<Mutation>>> l_mutStruct = l_dao
				.resolveSuperColumnFamily(l_mySupCF, l_timestamp);

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
		l_nameCol.setTimestamp(l_timestamp);
		Column l_ageCol = new Column(Charset.forName("UTF-8").encode("age"));
		l_ageCol.setValue(ByteArrayUtils.unsignedInttoByteArray(30));
		l_ageCol.setTimestamp(l_timestamp);
		
		
		Assert.assertThat(l_supCol.getColumns(), CoreMatchers.hasItems(l_nameCol, l_ageCol));
	}
	
	@Test
	public void testInsert() throws DaoException {
		MySuperColumn l_mySupCol = new MySuperColumn();
		l_mySupCol.age = 30;
		l_mySupCol.name = "lixl";
		l_mySupCol.key = "archicell";

		MySuperColumnFamily l_mySupCF = new MySuperColumnFamily();
		l_mySupCF.key = "beijing";
		l_mySupCF.mySupCol = l_mySupCol;

		EntityDao l_dao = new EntityDao();
		l_dao.setClientConnectionPool(connPool);
		l_dao.insert(l_mySupCF);
	}
}
