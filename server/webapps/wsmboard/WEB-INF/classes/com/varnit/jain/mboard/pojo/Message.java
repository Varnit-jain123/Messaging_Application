package com.varnit.jain.mboard.pojo;

public class Message implements java.io.Serializable 
{
    private String fromUser;
    private String message;

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }
}
