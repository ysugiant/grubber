package com.example.grubber;

public class FoodContent {
	String food_id;
	String name;
	String description;
	String vote;
	
	public FoodContent(String id, String n, String d, String v)
	{
		food_id = id;
		name = n;
		description = d;
		vote = v;
	}
	
	public String getFoodId()
	{
		return food_id;
	}
	
	public String getVote()
	{
		return vote;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
}
