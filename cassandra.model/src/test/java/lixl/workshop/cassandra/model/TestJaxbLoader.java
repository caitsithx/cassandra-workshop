package lixl.workshop.cassandra.model;
/*
 * Copyright (c) 2013 NO - All rights reserved.
 * This software is the confidential and proprietary information of 
 * NO ("Confidential Information"). You shall not disclose such 
 * Confidential Information and shall use it only in accordance with 
 * the terms of the license agreement you entered into with NO.
 *
 * $Revison$
 * $author$
 */


import java.util.List;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author <a href="mailto:caitsithx@live.cn">LI Xiaoliang </a>
 * 
 */
public class TestJaxbLoader {
	
	@Test
	public void testLoad() {
		ColumnFamilies l_cfs = null;
		try {
			l_cfs = JAXBLoader.loadJAXB("/sampleXML1.xml");
		} catch (JAXBException ex) {
			Assert.fail(ex.getMessage());
		}
		
		Assert.assertNotNull(l_cfs);
		
		List<ColumnFamily> cfList = l_cfs.getColumnFamily();
		Assert.assertEquals(1, cfList.size());
		Assert.assertEquals("employee", cfList.get(0).getName());
		
		List<Column> cList = cfList.get(0).getColumn();
		Assert.assertEquals(1, cList.size());
		Assert.assertEquals("hello", cList.get(0).getName());
		Assert.assertEquals(CassandraType.ASCII, cList.get(0).getType());
		Assert.assertTrue(cList.get(0).isPrimaryKey());
		
		System.err.println(l_cfs);
	}
}
