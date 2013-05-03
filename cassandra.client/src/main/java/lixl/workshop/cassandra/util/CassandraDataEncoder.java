/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import lixl.commons.bytes.ByteArrayUtils;
import lixl.workshop.cassandra.model.CassandraType;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public class CassandraDataEncoder {
	/**
	 * @param p_value
	 * @param p_type
	 * @return
	 */
	public final static byte[] encode(Object p_value, CassandraType p_type) {
		CassandraType l_type = p_type;
		if(l_type == null) {
			l_type = CassandraType.TEXT;
		}

		switch (l_type) {
		case TEXT:
			return getUTF8Bytes((String)p_value);
		case INT:
			return ByteArrayUtils.unsignedInttoByteArray((Integer)p_value);
		case ASCII:
			return Charset.forName("US-ASCII").encode((String)p_value).array();
		default:
			return getUTF8Bytes((String)p_value);
		}
	}
	
	private final static String UTF8 = "UTF-8";
	
	public final static byte[] getUTF8Bytes(String p_str) {
		byte[] l_bytes = null;
		
		try {
			l_bytes = p_str.getBytes(UTF8);
		} catch (UnsupportedEncodingException ex) {
			// normally shouldn't be here
			ex.printStackTrace();
		}
		
		return l_bytes;
	}

	public static ByteBuffer toByteBuffer(String value) {
		byte[] l_utf8bytes = null;
		try {
			l_utf8bytes = value.getBytes(UTF8);
		} catch (UnsupportedEncodingException ex) {
			// should never happen
			ex.printStackTrace();
		}
		
		return ByteBuffer.wrap(l_utf8bytes);
	}

	public static String toString(ByteBuffer buffer)
			throws UnsupportedEncodingException {
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		return new String(bytes, UTF8);
	}

}
