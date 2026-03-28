package com.varnit.jain.mboard.pojo.response;
public class LoginResponse implements java.io.Serializable
{
    private boolean success;
    private String [] onlineUsers;
    public boolean getSuccess() 
    {
        return this.success;
    }
    public void setSuccess(boolean success) 
    {
        this.success = success;
    }
    public String[] getOnlineUsers() 
    {
        return this.onlineUsers;
    }
    public void setOnlineUsers(String[] onlineUsers) 
    {
        this.onlineUsers = onlineUsers;
    }


}