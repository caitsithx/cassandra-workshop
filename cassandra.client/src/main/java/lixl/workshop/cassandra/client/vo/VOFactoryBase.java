/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.client.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import lixl.workshop.cassandra.client.util.CassandraDataEncoder;
import lixl.workshop.cassandra.model.CassandraType;
import lixl.workshop.cassandra.model.Column;
import lixl.workshop.cassandra.model.ColumnFamilies;
import lixl.workshop.cassandra.model.ColumnFamily;

import lixl.workshop.cassandra.model.JAXBLoader;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public abstract class VOFactoryBase {
	private final static Map<String, VOFactoryBase> VOFACTORIES = new HashMap<String, VOFactoryBase>();


	private Map<String, byte[]> m_encodedColumnNames = new HashMap<String, byte[]>();
	
	ColumnFamily m_definitionCache;
	
	Map<String, CassandraType> m_userColumnTypes = new HashMap<String, CassandraType>();
	CassandraType m_keyType = null;

	protected Map<String, CassandraType> getColumnTypes() {
		return m_userColumnTypes;
	}
	
	public ColumnFamily getColumnFamily() {
		return m_definitionCache;
	}

	protected CassandraType getkeyType() {
		return m_keyType;
	}

	public final static void loadDefinitions() throws VOFactoryException {
		loadDefinitionsFromRes("/lixl.workshop.cassandra.def.xml");
	}
	
	public final static void loadDefinitionsFromRes(String p_xmlLocation) throws VOFactoryException {
		synchronized (VOFACTORIES) {
			if(VOFACTORIES.size() == 0) {
				loadDefinitionsFromRes_int(p_xmlLocation);
			}
		}
	}
	
	public final static VOFactoryBase getVOFactory(String p_columnFamilyName) {
		return VOFACTORIES.get(p_columnFamilyName);
	}

	protected final static void loadDefinitionsFromRes_int(String p_xmlLocation) throws VOFactoryException {
		ColumnFamilies l_colFamesDef = null;
		try {
			l_colFamesDef = JAXBLoader.loadJAXB(p_xmlLocation);
		} catch (JAXBException ex) {
			throw new VOFactoryException("Vo Factory jaxb load err", ex);
		}
		
		for (ColumnFamily l_colFam : l_colFamesDef.getColumnFamily()) {
			EmbeddedVoFactory l_voFac = new EmbeddedVoFactory();

			l_voFac.m_name= l_colFam.getName();

			for (Column l_columnDef : l_colFam.getColumn()) {
				l_voFac.m_userColumnTypes.put(l_columnDef.getName(), l_columnDef.getType());

				if(l_columnDef.isPrimaryKey()) {
					l_voFac.m_keyType = l_columnDef.getType();
				}
			}
			
			l_voFac.m_definitionCache = l_colFam;
			
			VOFACTORIES.put(l_colFam.getName(), l_voFac);
		}
	}
	
	/**
	 * @param p_keyValue
	 * @param p_rawValues
	 * @return
	 */
	public final EncodedVO newVO(String p_keyValue, Map<String, Object> p_rawValues) {
		EncodedVO l_vo = new EncodedVO();

		l_vo.setKey(CassandraDataEncoder.encode(p_keyValue, getkeyType()));

		for (Entry<String, Object> l_rawColumnEntry : p_rawValues.entrySet()) {
			byte[] l_encodedColName = getEncodedColumnName(l_rawColumnEntry.getKey());
			byte[] l_encodedColVal = CassandraDataEncoder.encode(l_rawColumnEntry.getValue(),
					getColumnTypes().get(l_rawColumnEntry.getKey()));
			l_vo.addColumn(l_encodedColName, l_encodedColVal);
		}

		return l_vo;
	}

	private byte[] getEncodedColumnName(String p_name) {
		byte[] l_encoded = m_encodedColumnNames.get(p_name);

		if(l_encoded == null) {
			l_encoded = CassandraDataEncoder.encode(p_name, CassandraType.TEXT);
			m_encodedColumnNames.put(p_name, l_encoded);
		}

		return l_encoded;
	}

	static class EmbeddedVoFactory extends VOFactoryBase {
		public String m_name;

	}
}
