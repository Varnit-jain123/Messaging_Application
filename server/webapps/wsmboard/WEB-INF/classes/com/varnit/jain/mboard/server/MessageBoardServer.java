package com.varnit.jain.mboard.server;
import com.varnit.jain.mboard.pojo.*;
import com.varnit.jain.mboard.pojo.request.*;
import com.varnit.jain.mboard.pojo.response.*;
import com.varnit.jain.mboard.model.*;
import com.google.gson.*;
import com.google.gson.internal.*;
import java.io.*;
import java.util.*;
import javax.websocket.*;
import javax.websocket.server.*;
import javax.xml.crypto.Data;

@ServerEndpoint("/messageBoardServer")
public class MessageBoardServer
{
    private void login(Session session,String cardId,LoginRequest loginRequest)
    {
       User user=DataModel.getUser(loginRequest.getUsername());
       LoginResponse loginResponse=new LoginResponse();
       Gson gson=new Gson();
       if(user==null || !user.getPassword().equals(loginRequest.getPassword()))
       {
        System.out.println("Login Request Rejected");
        loginResponse.setSuccess(false);
       }
       else
       {
        Map<String,Object> m=session.getUserProperties();
        m.put("username", loginRequest.getUsername());
        System.out.println("Login Request Accepted");
        DataModel.addUser(loginRequest.getUsername(),session);
        loginResponse.setSuccess(true);
        loginResponse.setOnlineUsers(DataModel.getLoggedInUsers());
       }
       Card card=new Card();
       card.setType(CardType.RESPONSE);
       card.setAction(Action.LOGIN);
       card.setId(cardId);
       card.setObject(loginResponse);
       try{
        session.getBasicRemote().sendText(gson.toJson(card));   
       }catch(Exception e)
       {
        System.out.println(e);
       }
       if(loginResponse.getSuccess())
       {
        String u;
        int x=0;
        while(x<loginResponse.getOnlineUsers().length)
        {
            u=loginResponse.getOnlineUsers()[x];
            if(!u.equals(loginRequest.getUsername()))
            {
                Session s=DataModel.getUserSession(u);
                if(s!=null)
                {
                    AddUserRequest addUserRequest=new AddUserRequest();
                    addUserRequest.setUsername(loginRequest.getUsername());
                    Card c=new Card();
                    c.setType(CardType.REQUEST);
                    c.setId(UUID.randomUUID().toString());                    
                    c.setAction(Action.ADD_USER);
                    c.setObject(addUserRequest);
                    try{
                        synchronized(s)
                        {
                            s.getBasicRemote().sendText(gson.toJson(c));
                        }
                    }catch(Throwable t)
                    {
                        System.out.println(t);
                    }
                }
            }
            x++;
        }
       } // if part to update other connected users ends here
    }
    private void logout(Session session,String cardId,LogoutRequest logoutRequest)
    {
        DataModel.removeUser(logoutRequest.getUsername());
        String [] loggedInUsers=DataModel.getLoggedInUsers();
        String u;
        Gson gson=new Gson();
        int x=0;
        while(x<loggedInUsers.length)
        {
            u=loggedInUsers[x];
            Session s=DataModel.getUserSession(u);
            if(s!=null)
            {
                RemoveUserRequest removeUserRequest=new RemoveUserRequest();
                removeUserRequest.setUsername(logoutRequest.getUsername());
                Card c=new Card();
                c.setType(CardType.REQUEST);
                c.setId(UUID.randomUUID().toString());                    
                c.setAction(Action.REMOVE_USER);
                c.setObject(removeUserRequest);
                try{
                    synchronized(s)
                    {
                        s.getBasicRemote().sendText(gson.toJson(c));
                    }
                }catch(Throwable t)
                {
                    System.out.println(t);
                }
            }
            x++;
        }
    }
    private void postMessage(Session session,String cardId,PostMessageRequest postMessageRequest)
    {
        Message message=new Message();
        message.setFromUser(postMessageRequest.getFromUser());
        message.setMessage(postMessageRequest.getMessage());
        DataModel.addMessage(message);
        String [] loggedInUsers=DataModel.getLoggedInUsers();
        String u;
        Gson gson=new Gson();
        int x=0;
        while(x<loggedInUsers.length)
        {
            u=loggedInUsers[x];
            Session s=DataModel.getUserSession(u);
            if(s!=null)
            {
                Card c=new Card();
                c.setType(CardType.REQUEST);
                c.setId(UUID.randomUUID().toString());                    
                c.setAction(Action.ADD_MESSAGE);
                c.setObject(postMessageRequest);
                try{
                    synchronized(s)
                    {
                        s.getBasicRemote().sendText(gson.toJson(c));
                    }
                }catch(Throwable t)
                {
                    System.out.println(t);
                }
            }
            x++;
        }       
    }
    private void processRequest(Card card,Session session)
    {
       LinkedTreeMap map=(LinkedTreeMap)card.getObject();
       if(card.getAction().equals(Action.LOGIN))
       {
           LoginRequest loginRequest=new LoginRequest();
           loginRequest.setUsername((String)map.get("username"));
           loginRequest.setPassword((String)map.get("password"));
           login(session,card.getId(),loginRequest);
       }
       else if(card.getAction().equals(Action.LOGOUT))
       {
            LogoutRequest logoutRequest=new LogoutRequest();
            logoutRequest.setUsername((String)map.get("username"));
            logout(session,card.getId(),logoutRequest);
       }
       else if(card.getAction().equals(Action.POST_MESSAGE))
       {
            PostMessageRequest postMessageRequest=new PostMessageRequest();
            postMessageRequest.setFromUser((String)map.get("fromUser"));
            postMessageRequest.setMessage((String)map.get("message"));
            postMessage(session,card.getId(),postMessageRequest);
       }
       
    }
    private void processResponse(Card card,Session session)
    {
        //do nothing as of now
    }
    @OnMessage
    public void onMessage(String message,Session session)
    {
        System.out.println("Message arrived : " + message);
        try {
            Gson gson=new Gson();
            Card card=(Card)gson.fromJson(message,Card.class);
            if(card.getType().equals(CardType.REQUEST))
            {
                processRequest(card,session);
            }
            if(card.getType().equals(CardType.RESPONSE))
            {
                processResponse(card,session);
            }
        } catch (Throwable t) 
        {
            System.out.println(t);
        }
    }
    @OnError
    public void onError(Session session, Throwable t)
    {
        System.out.println("Some issue : " + t.toString());
    }
    @OnOpen
    public void onOpen()
    {
        System.out.println("Connection opened but do nothing as of now");
    }
    @OnClose
    public void onClose()
    {
        System.out.println("Connection closed but do nothing as of now");
    }
}