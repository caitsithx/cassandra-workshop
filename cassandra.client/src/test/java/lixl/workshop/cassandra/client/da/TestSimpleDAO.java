/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.da;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import lixl.workshop.cassandra.client.da.DaoException;
import lixl.workshop.cassandra.client.da.SimpleDao;
import lixl.workshop.cassandra.client.vo.EncodedVO;
import lixl.workshop.cassandra.client.vo.VOFactoryBase;
import lixl.workshop.cassandra.client.vo.VOFactoryException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">LI Xiaoliang </a>
 *
 */
public class TestSimpleDAO {
    private static Configuration CFG = null;

    private SimpleDao m_dao = null;
    
    @BeforeClass
    public static void beforeClass() throws ConfigurationException {
        CFG = new PropertiesConfiguration("test.properties");
    }

    @org.junit.Before
    public void before() throws VOFactoryException {
        m_dao = new SimpleDao();
        m_dao.setHost(CFG.getString("host"));
        m_dao.setPort(CFG.getInt("port"));
        m_dao.setKeySpace(CFG.getString("keyspace"));

        VOFactoryBase.loadDefinitionsFromRes("/employee.def.xml");
    }

    @org.junit.After
    public void after() {
        m_dao.closeConnection();
    }

//    @Test
//    public void testGetClient() {
//        try {
//            Assert.assertNotNull(m_dao.getClient());
//        } catch (DaoException ex) {
//            Assert.fail(ex.getMessage());
//        } finally {
//            m_dao.closeConnection();
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
            m_dao.insert(l_evo);
        } catch (DaoException ex) {
            Assert.fail(ex.getMessage());
        } 
    }

    @Test
    public void testBatchInsert() {
        VOFactoryBase l_voF = VOFactoryBase.getVOFactory("employee");

        try {
            m_dao.inserttoColumnFamily(buildVO(l_voF, 3));
        } catch (DaoException ex) {
            Assert.fail(ex.getMessage());
        } finally {
            m_dao.closeConnection();
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
            m_dao.inserttoColumnFamily(l_evo);
        } catch (DaoException ex) {
            Assert.fail(ex.getMessage());
        }
        
        EncodedVO l_foundVO =null;
        try {
            l_foundVO = m_dao.find(l_keyBytes);
        } catch (DaoException ex) {
            Assert.fail(ex.getMessage());
        }
        
        Assert.assertEquals(l_evo, l_foundVO);
        
    }
}
