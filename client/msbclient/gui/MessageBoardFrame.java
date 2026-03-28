import com.varnit.jain.mboard.pojo.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
interface MessageBoardEventHandler
{
    public void logout(String username);
    public void postMessage(String username,String message);
    // more method for sending messages
}
class MessageBoardFrame extends JFrame
{
    private MessageBoardEventHandler messageBoardEventHandler;
    private JTextArea messageBoardTextArea;
    private JTextArea messageToSendTextArea;
    private JLabel userLabel;
    private JList onlineUsersList;
    private JButton sendButton;
    private int frameWidth;
    private int frameHeight;
    private String username;
    private Container container;
    private Vector<String> users;
    MessageBoardFrame(String username)
    {
        this.username=username;    
        this.setTitle("Message Board - " + username);
        users=new Vector<String>();
        
        initComponents();
        addListeners();
        setIconImage(new ImageIcon("logo.png").getImage());
        setResizable(false);
        Dimension desktopDimension=Toolkit.getDefaultToolkit().getScreenSize();
        frameWidth=desktopDimension.width-50;
        frameHeight=desktopDimension.height-100;
        int x,y;
        x=desktopDimension.width/2-frameWidth/2;
        y=desktopDimension.height/2-frameHeight/2;
        setSize(frameWidth,frameHeight);
        setLocation(x,y);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }
    public void setMessageBoardEventHandler(MessageBoardEventHandler messageBoardEventHandler)
    {
        this.messageBoardEventHandler=messageBoardEventHandler;
    }
    private void initComponents()
    {
        container=getContentPane();
        messageBoardTextArea=new JTextArea();
        messageBoardTextArea.setEditable(false);
        container.setLayout(new BorderLayout());
        container.add(messageBoardTextArea, BorderLayout.CENTER);

        messageToSendTextArea=new JTextArea();
        sendButton=new JButton("SEND");
        JPanel panel1=new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.add(messageToSendTextArea, BorderLayout.CENTER);
        panel1.add(sendButton, BorderLayout.EAST);
        container.add(panel1, BorderLayout.SOUTH);
        userLabel=new JLabel("Online USERS");
        onlineUsersList=new JList(users);
        JPanel panel2=new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.add(userLabel, BorderLayout.NORTH);
        panel2.add(onlineUsersList, BorderLayout.CENTER);
        container.add(panel2, BorderLayout.EAST);
        
        Font font=new Font("verdana",Font.PLAIN,22);
        messageBoardTextArea.setFont(font);
        messageToSendTextArea.setFont(font);
        userLabel.setFont(font);
        onlineUsersList.setFont(font);
        sendButton.setFont(font);
        panel2.setPreferredSize(new Dimension(300,100));
        panel1.setPreferredSize(new Dimension(100,200));
        messageToSendTextArea.setBorder(BorderFactory.createLineBorder(Color.black));
        panel2.setBorder(BorderFactory.createLineBorder(Color.black));
        messageBoardTextArea.setBorder(BorderFactory.createLineBorder(Color.black));

    }
    public void addMessage(Message message)
    {
        String text=message.getFromUser() + " > " + message.getMessage() + "\n";
        try
        {
            messageBoardTextArea.getDocument().insertString(0,text,null);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    private void addListeners()
    {
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev)
            {
                String message=messageToSendTextArea.getText().trim();
                if(message.length()==0) return;
                messageBoardEventHandler.postMessage(username, message);
                messageToSendTextArea.setText("");
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev)
            {
                int selectedOption=JOptionPane.showConfirmDialog(MessageBoardFrame.this,"Logout ?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if(selectedOption==JOptionPane.YES_OPTION)
                {
                    if(MessageBoardFrame.this.messageBoardEventHandler!=null)
                    {
                        MessageBoardFrame.this.messageBoardEventHandler.logout(MessageBoardFrame.this.username);
                        MessageBoardFrame.this.dispose();   
                    }
                }
            }
        });
    }
    public void addUserToOnlineUsersList(String username)
    {
        Vector<String> v=new Vector<String>();
        for(String user:this.users)
        {
            v.add(user);
        }
        v.add(username);
        this.onlineUsersList.setListData(v);
        this.users=v;
    }
    public void removeUserFromOnlineUsersList(String username)
    {
        Vector<String> v=new Vector<String>();
        for(String user:this.users)
        {
            if(!user.equals(username))
            {
                if(user.equals(username)==false) v.add(user);
            }
        }
        this.onlineUsersList.setListData(v);
        this.users=v;
    }
    public void setOnlineUsers(String users[])
    {
        this.users=new Vector<String>();
        int i;
        for(i=0;i<users.length;i++)
        {
            this.users.add(users[i]);
        }
        this.onlineUsersList.setListData(this.users);
        // iterate users and populate the vector
        // and call setListData and pass vector to it 
    }
    // public static void main(String[] args)
    // {
    //     MessageBoardFrame mbf=new MessageBoardFrame("Whatever",null);
    //     mbf.setVisible(true);
    // }
}