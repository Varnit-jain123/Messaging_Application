import com.varnit.jain.mboard.pojo.*;
import com.varnit.jain.mboard.pojo.request.*;
import com.varnit.jain.mboard.pojo.response.*;
import com.google.gson.*;
import com.google.gson.internal.*;
import java.util.*;
import java.io.*;
import java.net.*;

import javax.websocket.*;

@ClientEndpoint
public class MSBClient implements LoginDialogEventHandler,MessageBoardEventHandler
{
    private LoginDialog loginDialog;
    private WebSocketContainer webSocketContainer;
    private Session session;
    private MessageBoardFrame messageBoardFrame;
    private String username;

    private void processAddUserRequest(AddUserRequest addUserRequest)
    {
        messageBoardFrame.addUserToOnlineUsersList(addUserRequest.getUsername());
    }
    private void processRemoveUserRequest(RemoveUserRequest removeUserRequest)
    {
        messageBoardFrame.removeUserFromOnlineUsersList(removeUserRequest.getUsername());
    }
    private void processPostMessageRequest(PostMessageRequest postMessageRequest)
    {
        Message message=new Message();
        message.setFromUser(postMessageRequest.getFromUser());
        message.setMessage(postMessageRequest.getMessage());
        messageBoardFrame.addMessage(message);
    }
    private void processLoginResponse(LoginResponse loginResponse)
    {
        if(loginResponse.getSuccess())
        {
            loginDialog.dispose();
            messageBoardFrame=new MessageBoardFrame(this.username);
            messageBoardFrame.setMessageBoardEventHandler(this);
            messageBoardFrame.setOnlineUsers(loginResponse.getOnlineUsers());
            messageBoardFrame.setVisible(true);
        }
        else
        {
            this.username=null;
            loginDialog.displayErrorMessage("Invalid username or password");
            loginDialog.clear();
            loginDialog.enableAll();
        }
    }
    private void processRequest(Card card)
    {
        LinkedTreeMap map=(LinkedTreeMap)card.getObject();
        if(card.getAction().equals(Action.ADD_USER))
        {
            AddUserRequest addUserRequest=new AddUserRequest();
            addUserRequest.setUsername(map.get("username").toString());
            processAddUserRequest(addUserRequest);
        }
        if(card.getAction().equals(Action.REMOVE_USER))
        {
            RemoveUserRequest removeUserRequest=new RemoveUserRequest();
            removeUserRequest.setUsername(map.get("username").toString());
            processRemoveUserRequest(removeUserRequest);
        }
        if(card.getAction().equals(Action.ADD_MESSAGE))
        {
            PostMessageRequest postMessageRequest=new PostMessageRequest();
            postMessageRequest.setFromUser(map.get("fromUser").toString());
            postMessageRequest.setMessage(map.get("message").toString());
            processPostMessageRequest(postMessageRequest);
        }
    }
    private void processResponse(Card card)
    {
       LinkedTreeMap map=(LinkedTreeMap)card.getObject();
       if(card.getAction().equals(Action.LOGIN))
       {
           LoginResponse loginResponse=new LoginResponse();
           loginResponse.setSuccess((Boolean)map.get("success"));
           if(loginResponse.getSuccess()==true)
           {
                ArrayList<String> listOfUsers=(ArrayList<String>)map.get("onlineUsers");
                loginResponse.setOnlineUsers(new String[listOfUsers.size()]);
                int x=0;
                for(Object obj:listOfUsers)
                {
                    loginResponse.getOnlineUsers()[x]=obj.toString();
                    x++;
                }
           }
           processLoginResponse(loginResponse);
       }
    }

    @OnMessage
    public void onMessage(String message)
    {
        System.out.println("Message arrived : " + message);
        try {
            Gson gson=new Gson();
            Card card=(Card)gson.fromJson(message,Card.class);
            if(card.getType().equals(CardType.REQUEST))
            {
                processRequest(card);
            }
            if(card.getType().equals(CardType.RESPONSE))
            {
                processResponse(card);
            }
        } catch (Throwable t) 
        {
            System.out.println(t);
        }

        //parse the message to a card 
        // then detemine if it is a request or response
        // and parse to appropriate type

        // for now
        // if it is Login Response then 
        // check if success is false call show error ,clear,enable all
        //                      true call dispose login dialog using the dispose method
        //                            create message board frame object ,
        //                            pass username to constructor
        //                            from loginresponse extract online users list and pass to to sentOnlineUsers of messageBoardFrame


        // System.out.println("Received: " + message);

    }

    private void connect()
    {
        try {
            webSocketContainer=ContainerProvider.getWebSocketContainer();
            session=webSocketContainer.connectToServer(this, URI.create("ws://localhost:8081/wsmboard/messageBoardServer"));
        } catch (Throwable t) 
        {
            System.out.println(t);
            System.exit(0);
        }
    }
    private void createTUI()
    {
        try
        {
            InputStreamReader inputStreamReader;
            inputStreamReader=new InputStreamReader(System.in);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            String input,command,username,password;
            String prompt="::MsgBoard>";
            String splits[];
            Gson gson=new Gson();
            while(true)
            {
                System.out.print(prompt);
                input=bufferedReader.readLine();
                if(input.equalsIgnoreCase("quit")) break;
                splits=input.split(" ");
                command=splits[0];
                if(command.equals("login"))
                {
                    username=splits[1];
                    password=splits[2];
                    LoginRequest loginRequest=new LoginRequest();
                    loginRequest.setUsername(username);
                    loginRequest.setPassword(password);
                    Card card=new Card();
                    card.setType(CardType.REQUEST);
                    card.setId(UUID.randomUUID().toString());
                    card.setAction(Action.LOGIN);
                    card.setObject(loginRequest);
                    session.getBasicRemote().sendText(gson.toJson(card));
                }
                else if(command.equals("logout"))
                {
                    username=splits[1];
                    LogoutRequest logoutRequest=new LogoutRequest();
                    logoutRequest.setUsername(username);
                    Card card=new Card();
                    card.setType(CardType.REQUEST);
                    card.setId(UUID.randomUUID().toString());
                    card.setAction(Action.LOGOUT);
                    card.setObject(logoutRequest);
                    session.getBasicRemote().sendText(gson.toJson(card));
                }
            }

        }catch(Throwable t)
        {
            System.out.println(t);
            javax.swing.JOptionPane.showMessageDialog(null,"Unable to connect to server");
            System.exit(0);
        }
    }
    private void start()
    {
        if(this.loginDialog==null)
        {
            this.loginDialog=new LoginDialog();
            this.loginDialog.setLoginDialogEventHandler(this);
        }
        this.loginDialog.setVisible(true);
    }
    public void onLoginDialogWindowClosed()
    {
        if(session!=null)
        {
            try
            {
                session.close();
            }catch(Throwable t)
            {
                System.out.println(t);
            }
            
        }
        System.exit(0);
    }
    public void onLoginButtonClicked(String username,String password)
    {
        System.out.println("Login button clicked with username : " + username + " and password : " + password);
        this.username=username;
        Gson gson=new Gson();
        LoginRequest loginRequest=new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        Card card=new Card();
        card.setType(CardType.REQUEST);
        card.setId(UUID.randomUUID().toString());
        card.setAction(Action.LOGIN);
        card.setObject(loginRequest);
        try{
        session.getBasicRemote().sendText(gson.toJson(card));
        System.out.println("Login Card sent");
        }catch(Throwable t)
        {
            loginDialog.displayErrorMessage("Unable to login, try after some time");
            loginDialog.clear();
            loginDialog.enableAll();
        }
    }
    public void logout(String username)
    {
        Gson gson=new Gson();
        LogoutRequest logoutRequest=new LogoutRequest();
        logoutRequest.setUsername(username);
        Card card=new Card();
        card.setType(CardType.REQUEST);
        card.setId(UUID.randomUUID().toString());
        card.setAction(Action.LOGOUT);
        card.setObject(logoutRequest);
        try{
        session.getBasicRemote().sendText(gson.toJson(card));
        System.out.println("Logout Card sent to server");
        }catch(Throwable t)
        {
        }   
        System.exit(0);
    }
    public void postMessage(String fromUsername,String message)
    {
        Gson gson=new Gson();
        PostMessageRequest postMessageRequest=new PostMessageRequest();
        postMessageRequest.setFromUser(fromUsername);
        postMessageRequest.setMessage(message);
        Card card=new Card();
        card.setType(CardType.REQUEST);
        card.setId(UUID.randomUUID().toString());
        card.setAction(Action.POST_MESSAGE);
        card.setObject(postMessageRequest);
        try{
        session.getBasicRemote().sendText(gson.toJson(card));
        System.out.println("Post Message Card sent");
        }catch(Throwable t)
        {
            System.out.println(t);
           // do nothing
        }
    }
    public static void main(String[] args)
    {
        MSBClient msbClient=new MSBClient();
        msbClient.connect();
        msbClient.start();
        //msbClient.createTUI();
    }
}