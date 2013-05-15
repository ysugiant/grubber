package com.example.grubber;

public class ResultContent {
	String name;
	String address;
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
	
	public ResultContent(String name, String address, String distance) {
		setName(name);
		setAddress(address);
		setDistance(distance);
	}
	
	// image
	
}
