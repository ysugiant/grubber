package com.example.grubber;

public class TopFoodContent {
	String food_id;
	String food_name;
	String rest_name;
	String comments;
	String vote;
	
	public TopFoodContent(String id, String fn, String rn, String c, String v)
	{
		food_id = id;
		food_name = fn;
		rest_name = rn;
		comments = c;
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
}
