/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.simple;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import lixl.workshop.cassandra.client.DaoException;
import lixl.workshop.cassandra.client.simple.EncodedVO;
import lixl.workshop.cassandra.client.simple.SimpleDao;
import lixl.workshop.cassandra.client.simple.VOFactoryBase;
import lixl.workshop.cassandra.client.simple.VOFactoryException;
import lixl.workshop.cassandra.connection.ClientConnectionFactory;
import lixl.workshop.cassandra.connection.ClientConnectionPool;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">LI Xiaoliang </a>
 *
 */
public class TestSimpleDAO {
    private static Configuration CFG = null;
    private static ClientConnectionPool connPool = null;
    
    private SimpleDao simpleDao = null;
    
    @BeforeClass
    public static void beforeClass() throws ConfigurationException, VOFactoryException {
        CFG = new PropertiesConfiguration("test.properties"); //$NON-NLS-1$
        VOFactoryBase.loadDefinitionsFromRes("/employee.def.xml");
        
        ClientConnectionFactory l_ccf = new ClientConnectionFactory();
    	l_ccf.setConfiguration(CFG);
    	connPool = new ClientConnectionPool(l_ccf);
    }

    @org.junit.AfterClass
    public static void after() throws Exception {
    	connPool.close();
    }
    
    @org.junit.Before
    public void before() {
        simpleDao = new SimpleDao();
        simpleDao.setClientConnectionPool(connPool);
//        simpleDao.setHost(CFG.getString("host"));
//        simpleDao.setPort(CFG.getInt("port"));
//        simpleDao.setKeySpace(CFG.getString("keyspace"));
    }

//    @Test
//    public void testGetClient() {
//        try {
//            Assert.assertNotNull(simpleDao.getClient());
//        } catch (DaoException ex) {
//            Assert.fail(ex.getMessage());
//        } finally {
//            simpleDao.closeConnection();
//        }
//    }

    @Test
    public void testInsert() {
        VOFactoryBase l_voF = VOFactoryBase.getVOFactory("employee");

        Map<String, Object> l_values = new HashMap<>();
        l_values.put("age", 13);
        l_values.put("address", "foo address");
        EncodedVO l_evo = l_voF.newVO("Testuser", l_values);

        try {
            simpleDao.insert(l_evo);
        } catch (DaoException ex) {
            Assert.fail(ex.getMessage());
        } 
    }

    @Test
    public void testBatchInsert() {
        VOFactoryBase l_voF = VOFactoryBase.getVOFactory("employee");

        try {
            simpleDao.inserttoColumnFamily(buildVO(l_voF, 3));
        } catch (DaoException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    private EncodedVO[] buildVO(VOFactoryBase l_voF, int p_count) {
        EncodedVO[] l_voList = new EncodedVO[p_count];

        for (int i = 0; i < p_count; i++) {
            Map<String, Object> l_values = new HashMap<>();
            l_values.put("age", i);
            l_values.put("address", "foo address, No." + i);
            l_voList[i] = l_voF.newVO("Testuser No." + i, l_values);
        }

        return l_voList;
    }

    @Test
    public void testFind() {
        String l_keyStr = "testFind";
        byte[] l_keyBytes = Charset.forName("UTF-8").encode(l_keyStr).array();
        
         VOFactoryBase l_voF = VOFactoryBase.getVOFactory("employee");

        Map<String, Object> l_values = new HashMap<>();
        l_values.put("age", 13);
        l_values.put("address", "foo address");
        EncodedVO l_evo = l_voF.newVO(l_keyStr, l_values);
        try {
            simpleDao.inserttoColumnFamily(l_evo);
        } catch (DaoException ex) {
            Assert.fail(ex.getMessage());
        }
        
        EncodedVO l_foundVO =null;
        try {
            l_foundVO = simpleDao.find(l_keyBytes);
        } catch (DaoException ex) {
            Assert.fail(ex.getMessage());
        }
        
        Assert.assertEquals(l_evo, l_foundVO);
        
    }
}
