package lixl.workshop.cassandra.model;
/*
 * Copyright (c) 2013 Gemalto - All rights reserved.
 * This software is the confidential and proprietary information of 
 * Gemalto ("Confidential Information"). You shall not disclose such 
 * Confidential Information and shall use it only in accordance with 
 * the terms of the license agreement you entered into with Gemalto.
 *
 */


import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import lixl.workshop.cassandra.model.ColumnFamilies;

import org.xml.sax.SAXException;


/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">LI Xiaoliang </a>
 * 
 */
public class JAXBLoader {

	public final static ColumnFamilies loadJAXB(String p_xmlLocation)
			throws JAXBException {
		ColumnFamilies fl = null;

		InputStream l_defStream = JAXBLoader.class.getResourceAsStream(p_xmlLocation);
		JAXBContext jaxbContext = JAXBContext.newInstance(ColumnFamilies.class);
		StreamSource stremSource = new StreamSource(l_defStream);
		// unmarshal
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		
		try{
		SchemaFactory l_sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema l_schema = l_sf.newSchema(new StreamSource(
				JAXBLoader.class.getResourceAsStream("/lixl.workshop.cassandra.def.xsd")));
		unmarshaller.setSchema(l_schema);
		} catch (SAXException ex) {
			//TODO
			ex.printStackTrace();
		}
		
		JAXBElement<ColumnFamilies> root = unmarshaller.unmarshal(stremSource, ColumnFamilies.class);
//		fl = (ColumnFamilies) unmarshaller.unmarshal(stremSource);

		fl = root.getValue();
		return fl;
	}
}
