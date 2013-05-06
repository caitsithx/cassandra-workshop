/*
 * Copyright (c) 2013 - caitsithx@live.cn.
 *
 */
package lixl.workshop.cassandra.client.simple;

import java.util.Map.Entry;

import junit.framework.Assert;
import lixl.workshop.cassandra.client.simple.VOFactoryBase;
import lixl.workshop.cassandra.client.simple.VOFactoryException;
import lixl.workshop.cassandra.model.CassandraType;


/**
 * @author <a href="mailto:caitsithx@live.cn">LI Xiaoliang </a>
 *
 */
public class TestVOFactoryBase {

	public void testVOFactory() {
		try {
			VOFactoryBase.loadDefinitionsFromRes("/employee.def.xml");
		} catch (VOFactoryException ex) {
			Assert.fail(ex.getMessage());
		}
		
		VOFactoryBase l_vof = VOFactoryBase.getVOFactory("employee");
		Assert.assertNotNull(l_vof);
		Assert.assertEquals(CassandraType.TEXT, l_vof.getkeyType());
		Assert.assertEquals(3, l_vof.getColumnTypes().size());
		
		Object[] l_columns = l_vof.getColumnTypes().entrySet().toArray();  
		
		Entry<String, CassandraType> l_voDef = (Entry<String, CassandraType>)l_columns[0];
		Assert.assertEquals("address", l_voDef.getKey());
		Assert.assertEquals(CassandraType.TEXT, l_voDef.getValue());
		
		l_voDef = (Entry<String, CassandraType>)l_columns[1];
		Assert.assertEquals("age", l_voDef.getKey());
		Assert.assertEquals(CassandraType.INT, l_voDef.getValue());
		
		l_voDef = (Entry<String, CassandraType>)l_columns[2];
		Assert.assertEquals("name", l_voDef.getKey());
		Assert.assertEquals(CassandraType.TEXT, l_voDef.getValue());
	}

}
