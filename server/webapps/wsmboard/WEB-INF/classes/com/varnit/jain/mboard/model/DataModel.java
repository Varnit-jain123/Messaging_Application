package com.varnit.jain.mboard.model;
import com.varnit.jain.mboard.pojo.*;
import java.util.*;
import javax.websocket.*;
public class DataModel 
{
    static private Map<String,User> registeredUsers=new HashMap<>();
    static private Map<String,Session> loggedInUsers=new HashMap<>();
    static private Map<String,Card> unProcessedRequests=new HashMap<>();
    static private LinkedList<Message> message=new LinkedList<>();
    static{
        initDataModel();
    }
    public static void addMessage(Message msg)
    {
        synchronized(message)
        {
            message.addFirst(msg);
        }
    }
    public static Message[] getMessages(int numberOfLastMessages)
    {
        synchronized(message)
        {
            if(numberOfLastMessages>message.size())
            {
                numberOfLastMessages=message.size();
            }
            Message [] msgs=new Message[numberOfLastMessages];
            for(int i=0;i<numberOfLastMessages;i++)
            {
                msgs[i]=message.get(i);
            }
            return msgs;
        }

    }
    public static void addUser(String username,Session session)
    {
        synchronized(loggedInUsers)
        {
            loggedInUsers.put(username,session);
        }
    }
    public static void removeUser(String username)
    {
        synchronized(loggedInUsers)
        {
            loggedInUsers.remove(username);
        }
    }
    public static void addUnProcessedRequest(Card card)
    {
        synchronized(unProcessedRequests)
        {
            unProcessedRequests.put(card.getId(),card);
        }
    }
    public static void removeUnProcessedRequest(String id)
    {
        synchronized(unProcessedRequests)
        {
            unProcessedRequests.remove(id);
        }
    }
    public static Card getUnProcessedRequest(String id)
    {
        synchronized(unProcessedRequests)
        {
            return unProcessedRequests.get(id);
        }
    }
    public static String[] getLoggedInUsers() {
        SortedSet<String> tree=new TreeSet<String>();
        synchronized(loggedInUsers)
        {
            for(String username:loggedInUsers.keySet())
            {
                tree.add(username);
            }
        }
        String users[]=new String[tree.size()];
        tree.toArray(users);
        return users;
    }
    public static User getUser(String username)
    {
        synchronized(registeredUsers)
        {
            return registeredUsers.get(username);
        }
    }
    public static Session getUserSession(String username)
    {
        synchronized(loggedInUsers)
        {
            return loggedInUsers.get(username);
        }
    }
    private static void initDataModel()
    {
        //I am hardcoding
        User user1=new User();
        user1.setUsername("varnit");
        user1.setPassword("varnit");
        User user2=new User();
        user2.setUsername("tubhyam");
        user2.setPassword("tubhyam");
        User user3=new User();
        user3.setUsername("alice");
        user3.setPassword("alice");
        User user4=new User();
        user4.setUsername("bob");
        user4.setPassword("bob");
        User user5=new User();
        user5.setUsername("charlie");   
        user5.setPassword("charlie");
        User user6=new User();
        user6.setUsername("dave");
        user6.setPassword("dave");
        User user7=new User();
        user7.setUsername("eve");
        user7.setPassword("eve");
        User user8=new User();
        user8.setUsername("mallory");
        user8.setPassword("mallory");
        User user9=new User();
        user9.setUsername("trent");
        user9.setPassword("trent");
        User user10=new User();
        user10.setUsername("peggy");
        user10.setPassword("peggy");

        registeredUsers.put(user1.getUsername(),user1);
        registeredUsers.put(user2.getUsername(),user2);
        registeredUsers.put(user3.getUsername(),user3);
        registeredUsers.put(user4.getUsername(),user4);
        registeredUsers.put(user5.getUsername(),user5);
        registeredUsers.put(user6.getUsername(),user6);
        registeredUsers.put(user7.getUsername(),user7);
        registeredUsers.put(user8.getUsername(),user8);
        registeredUsers.put(user9.getUsername(),user9);
        registeredUsers.put(user10.getUsername(),user10);
    }
        
}
