/*
 * Copyright (c) 2013 - caitsithx@live.cn.
 *
 */
package com.cassandraguide.hotel;


import lixl.workshop.cassandra.client.jpalike.ColumnFamily_A;
import lixl.workshop.cassandra.client.jpalike.Column_A;
import lixl.workshop.cassandra.client.jpalike.Key_A;
import lixl.workshop.cassandra.client.jpalike.SuperColumn_A;
import lixl.workshop.cassandra.model.CassandraType;

/**
 * @author <a href="mailto:caitsithx@live.cn"> lixl </a>
 *
 */
@ColumnFamily_A(name="PointOfInterest", isSuper=true)
public class PointOfInterest {
	
	@Key_A
	private String poiName;
	
	private Hotel hotel;

	/**
	 * @return the hotel
	 */
	public Hotel getHotel() {
		return this.hotel;
	}

	/**
	 * @param p_hotel the hotel to set
	 */
	public void setHotel(Hotel p_hotel) {
		this.hotel = p_hotel;
	}

	/**
	 * @return the poiName
	 */
	public String getPoiName() {
		return this.poiName;
	}

	/**
	 * @param p_poiName the poiName to set
	 */
	public void setPoiName(String p_poiName) {
		this.poiName = p_poiName;
	}

	@SuperColumn_A
	class Hotel {
		@Key_A
		private String hotelName;
		
		/**
		 * @return the hotelName
		 */
		public String getHotelName() {
			return this.hotelName;
		}
		/**
		 * @param p_hotelName the hotelName to set
		 */
		public void setHotelName(String p_hotelName) {
			this.hotelName = p_hotelName;
		}
		@Column_A(name="description", type=CassandraType.TEXT)
		private String description;
		
		@Column_A(name="phone", type=CassandraType.TEXT)
		private String phone;
		
		/**
		 * @return the description
		 */
		public String getDescription() {
			return this.description;
		}
		/**
		 * @param p_description the description to set
		 */
		public void setDescription(String p_description) {
			this.description = p_description;
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
	}
}
