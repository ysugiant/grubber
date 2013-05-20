package com.example.grubber;

public class ResultContent {
	String rest_id;
	String name;
	String address;
	String city;
	String state;
	String zip;
	String latitude;
	String longitude;
	String phone;
	String website;
	String distance;
	
	public String getName() {
		return name;
	}
	private void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	private void setAddress(String address) {
		this.address = address;
	}
	public String getDistance() {
		return distance;
	}
	private void setDistance(String distance) {
		this.distance = distance;
	}

	public String getId() {
		return rest_id;
	}
	private void setId(String id) {
		this.rest_id = id;
	}
	public String getCity() {
		return city;
	}
	private void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	private void setState(String state) {
		this.state = state;
	}
	public String getLatitude() {
		return latitude;
	}
	private void setLatitude(String lat) {
		this.latitude = lat;
	}
	public String getLongitude() {
		return longitude;
	}
	private void setLongitude(String lng) {
		this.longitude = lng;
	}
	public String getZip() {
		return zip;
	}
	private void setZip(String zip) {
		this.zip = zip;
	}
	public String getPhone() {
		return phone;
	}
	private void setPhone(String phone) {
		this.phone = phone;
	}
	public String getWebsite() {
		return website;
	}
	private void setWebsite(String website) {
		this.website = website;
	}
	public ResultContent(String id, String name, String address, String city, String state, String zip, String longitude, String latitude, String phone, String website, String distance) {
		setId(id);
		setName(name);
		setAddress(address);
		setCity(city);
		setState(state);
		setZip(zip);
		setLongitude(longitude);
		setLatitude(latitude);
		setPhone(phone);
		setWebsite(website);
		setDistance(distance);
	}
	
	// image
	
}
