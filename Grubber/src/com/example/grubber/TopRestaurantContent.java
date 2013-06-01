package com.example.grubber;

public class TopRestaurantContent {
	String rest_name;
	String vote;
	String rest_id;
	String address;
	String city;
	String state;
	String zip;
	String lat;
	String longt;
	String phone;
	String website;

	public TopRestaurantContent(String id, String v, String rn, String addr,
			String city, String state, String zip, String lat, String longt,
			String phone, String website) {
		rest_id = id;
		rest_name = rn;
		vote = v;
		address = addr;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.lat = lat;
		this.longt = longt;
		this.phone = phone;
		this.website = website;
	}

	public String getVote() {
		return vote;
	}

	public String getRestName() {
		return rest_name;
	}

	public String getRestId() {
		return rest_id;
	}

	public String getAddress() {
		return address;
	}

	public String getState() {
		return state;
	}

	public String getZip() {
		return zip;
	}

	public String getWebsite() {
		return website;
	}

	public String getPhone() {
		return phone;
	}

	public String getLongitude() {
		return longt;
	}

	public String getLatitude() {
		return lat;
	}

	public String getCity() {
		return city;
	}
}
