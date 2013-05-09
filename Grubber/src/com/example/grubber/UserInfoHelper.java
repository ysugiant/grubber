package com.example.grubber;

import android.app.Application;


public class UserInfoHelper  {
	private static UserInfoHelper instance;
	
	private String m_lastName;
	private String m_firstName;
	private String m_email;
	private String m_userName;
	private boolean is_signIn;
	private int m_userId;
	
	public static synchronized UserInfoHelper getInstance(){
		
		if(instance==null){
		       instance = new UserInfoHelper();
		}
		return instance;
	}
	public  UserInfoHelper (){
		this.m_userName = "";
		this.m_email = "";
		this.m_firstName = "";
		this.m_lastName = "";
		this.is_signIn = false;
	 }
	
	//set every user info
	public void setAll (int userid,String username, String email,String firstname, String lastname){
		this.m_userId = userid;
		this.m_userName = username;
		this.m_email = email;
		this.m_firstName = firstname;
		this.m_lastName = lastname;
		this.is_signIn = true;
    }
	
	
	
	
	
	//set every user info expect the userID, 
	public void setAll (String username, String email,String firstname, String lastname){
		this.m_userName = username;
		this.m_email = email;
		this.m_firstName = firstname;
		this.m_lastName = lastname;
		this.is_signIn = true;
    }
	
	public void userLogin(int id){
		this.m_userId = id;
		this.is_signIn = true;
	}
	
	public void setUserID(int userid){
		this.m_userId = userid;
	}
	
	 public void setUsername(String username){
		this.m_userName = username;
	}
	
	public void setEmail(String email){
		this.m_email = email;
	}
	public void setLastName(String lastName){
		this.m_lastName = lastName;
	}
	public void setFirstName(String firstName){
		this.m_firstName = firstName;
	}
	
	
	public String getUserName(){
		return this.m_userName;
	}
	public String getEmail(){
		return this.m_email;
	}
	public String getFirstName(){
		return this.m_firstName;
	}
	public String getLastName(){
		return this.m_lastName;
	}
	public Boolean isSignIn(){
		return this.is_signIn;
	}
	public int getUserID(){
		return this.m_userId;
	}
	
	
	public void signOut(){
		this.m_userId = 0;
		this.m_userName = "";
		this.m_email = "";
		this.m_firstName = "";
		this.m_lastName = "";
		this.is_signIn = false;
	}
	
}
