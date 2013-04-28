/*
 * Copyright (c) 2013 - xiaoliang.li@gemalto.com.
 *
 */
package com.cassandraguide.hotel;


import lixl.workshop.cassandra.client.ColumnFamily_A;
import lixl.workshop.cassandra.client.Column_A;
import lixl.workshop.cassandra.client.Key_A;
import lixl.workshop.cassandra.model.CassandraType;

/**
 * @author <a href="mailto:xiaoliang.li@gemalto.com">lixl </a>
 *
 */

@ColumnFamily_A(name="Hotel")
public class Hotel {

	@Key_A
	private String id;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param p_id the id to set
	 */
	public void setId(String p_id) {
		this.id = p_id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param p_name the name to set
	 */
	public void setName(String p_name) {
		this.name = p_name;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return this.phone;
	}

	/**
	 * @param p_phone the phone to set
	 */
	public void setPhone(String p_phone) {
		this.phone = p_phone;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * @param p_address the address to set
	 */
	public void setAddress(String p_address) {
		this.address = p_address;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * @param p_city the city to set
	 */
	public void setCity(String p_city) {
		this.city = p_city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * @param p_state the state to set
	 */
	public void setState(String p_state) {
		this.state = p_state;
	}

	/**
	 * @return the zip
	 */
	public String getZip() {
		return this.zip;
	}

	/**
	 * @param p_zip the zip to set
	 */
	public void setZip(String p_zip) {
		this.zip = p_zip;
	}

	@Column_A(name="name", type=CassandraType.TEXT)
	private String name;
	
	@Column_A(name="phone", type=CassandraType.TEXT)
	private String phone;
	
	@Column_A(name="address", type=CassandraType.TEXT)
	private String address;
	
	@Column_A(name="city", type=CassandraType.TEXT)
	private String city;
	
	@Column_A(name="state", type=CassandraType.TEXT)
	private String state;
	
	@Column_A(name="zip", type=CassandraType.TEXT)
	private String zip;
}
