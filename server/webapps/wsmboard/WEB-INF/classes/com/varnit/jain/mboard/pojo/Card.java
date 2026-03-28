package com.varnit.jain.mboard.pojo;

public class Card implements java.io.Serializable
{
    private String type;  // REQUEST or RESPONSE
    private String id; // UUID
    private String action;
    private Object object;

    // getters and setters
    public String getType() 
    {
        return this.type;
    }
    public void setType(String type) 
    {
        this.type = type;
    }
    public String getId() 
    {
        return this.id;
    }
    public void setId(String id) 
    {
        this.id = id;
    }
    public String getAction() 
    {
        return this.action;
    }
    public void setAction(String action) 
    {
        this.action = action;
    }
    public Object getObject() 
    {
        return this.object;
    }
    public void setObject(Object object) 
    {
        this.object = object;
    }
}
