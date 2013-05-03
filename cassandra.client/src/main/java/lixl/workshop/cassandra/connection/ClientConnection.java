/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package lixl.workshop.cassandra.connection;

import lixl.workshop.cassandra.client.DaoException;

import org.apache.cassandra.thrift.Cassandra.Client;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;


/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */
public class ClientConnection {

	private String m_host = "sinrndvud071";
	private int m_port = 24033;
	private String m_keySpace = "Twissandra";
	private TTransport m_tTransport = null;
	private Client m_client = null;

	/**
	 * 
	 */
	public ClientConnection() {
		super();
	}

	/**
	 * @param p_keySpace the keySpace to set
	 */
	public void setKeySpace(String p_keySpace) {
		this.m_keySpace = p_keySpace;
	}

	/**
	 * @param p_host the host to set
	 */
	public void setHost(String p_host) {
		this.m_host = p_host;
	}

	/**
	 * @param p_port the port to set
	 */
	public void setPort(int p_port) {
		this.m_port = p_port;
	}

	private TTransport getTransport() throws DaoException {
		if(m_tTransport == null) {
			m_tTransport = new TFramedTransport(new TSocket(m_host, m_port));
	
			try {
				m_tTransport.open();
			} catch (TTransportException ex) {
				m_tTransport = null;
				throw new DaoException("open connection error", ex);
			} 
		}
	
		return m_tTransport;
	}

	public final Client getClient() throws DaoException {
		if(m_client == null) {
			TProtocol proto = new TBinaryProtocol(getTransport());
			m_client = new Client(proto);
			try {
				m_client.set_cql_version("2.0.0");//this version number is ok
				m_client.set_keyspace(m_keySpace);
			} 
			catch (Exception ex)
			/*catch (InvalidRequestException | TException ex)*/ {
				m_client = null;
				throw new DaoException("set keyspace error", ex);
			} 
		}
	
		return m_client;
	}


	public void closeConnection() {
		if (m_tTransport != null) {
			m_tTransport.close();
			m_tTransport = null;
			m_client = null;
		}
	}
}