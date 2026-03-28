package com.varnit.jain.mboard.pojo.request;

public class PostMessageRequest implements java.io.Serializable 
{
    private String fromUser;
    private String message;
    public String getFromUser() 
    {
        return fromUser;
    }   
    public void setFromUser(String fromUser) 
    {
        this.fromUser = fromUser;
    }
    public String getMessage() 
    {
        return message;
    }
    public void setMessage(String message) 
    {
        this.message = message;
    }    
}
