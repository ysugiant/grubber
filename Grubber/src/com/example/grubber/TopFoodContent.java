package com.example.grubber;

public class TopFoodContent {
	String food_id;
	String food_name;
	String rest_name;
	String comments;
	String vote;
	String description;
	String rest_id;
	
	public TopFoodContent(String id, String fn, String rn, String c, String v, String d, String r)
	{
		food_id = id;
		food_name = fn;
		rest_name = rn;
		comments = c;
		vote = v;
		description = d;
		rest_id = r;
	}
	
	public String getFoodId()
	{
		return food_id;
	}
	
	public String getVote()
	{
		return vote;
	}
	
	public String getFoodName()
	{
		return food_name;
	}
	
	public String getRestName()
	{
		return rest_name;
	}
	
	public String getComments()
	{
		return comments;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public String getRestId() 
	{
		return rest_id;
	}
}
