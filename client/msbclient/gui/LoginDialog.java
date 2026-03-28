import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
interface LoginDialogEventHandler
{
    public void onLoginButtonClicked(String username,String password);
    public void onLoginDialogWindowClosed();
}
class LoginDialog extends JDialog
{
    private LoginDialogEventHandler loginDialogEventHandler;
    private Image image;
    private JLabel titleLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private Container container;
    private int dialogWidth=500;
    private int dialogHeight=300;
    LoginDialog()
    {
        super(new JFrame(),"Message Board",true);
        image=new ImageIcon("logo.png").getImage();
        ((JFrame)getOwner()).setIconImage(image);
        initComponents();
        addListeners();
        Dimension desktopDimension=Toolkit.getDefaultToolkit().getScreenSize();
        setSize(dialogWidth,dialogHeight);
        setResizable(false);
        int x,y;
        x=desktopDimension.width/2-dialogWidth/2;
        y=desktopDimension.height/2-dialogHeight/2;
        setLocation(x,y);
    }
    public void setLoginDialogEventHandler(LoginDialogEventHandler loginDialogEventHandler)
    {
        this.loginDialogEventHandler=loginDialogEventHandler;
    }
    private void initComponents()
    {
        titleLabel=new JLabel("Authentication");
        usernameLabel=new JLabel("Username");
        passwordLabel=new JLabel("Password");
        usernameTextField=new JTextField();
        passwordField=new JPasswordField();
        loginButton=new JButton("Login");
        container=getContentPane();
        container.setLayout(null);

        titleLabel.setBounds(142,20,250,50);
        Font titleFont=new Font("Verdana",Font.BOLD,24);
        titleLabel.setFont(titleFont);
        container.add(titleLabel);

        Font font=new Font("Verdana",Font.PLAIN,20);
        usernameLabel.setFont(font);
        usernameLabel.setBounds(20,80,150,30);
        container.add(usernameLabel);

        usernameTextField.setFont(font);
        usernameTextField.setBounds(180,80,230,30);
        container.add(usernameTextField);

        passwordLabel.setFont(font);
        passwordLabel.setBounds(20,120,150,30);
        container.add(passwordLabel);

        passwordField.setFont(font);
        passwordField.setBounds(180,120,230,30);
        container.add(passwordField);

        loginButton.setFont(font);
        loginButton.setBounds(200,160,150,30);
        container.add(loginButton);
    }
    private void addListeners()
    {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev)
            {
                loginDialogEventHandler.onLoginDialogWindowClosed();
            }
            
        });
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev)
            {
                String username=LoginDialog.this.usernameTextField.getText().trim();
                if(username.length()==0)
                {
                    JOptionPane.showMessageDialog(LoginDialog.this,"Username cannot be empty");
                    LoginDialog.this.usernameTextField.requestFocus();
                    return;
                }
                String password=new String(LoginDialog.this.passwordField.getPassword()).trim();
                if(password.length()==0)
                {
                    JOptionPane.showMessageDialog(LoginDialog.this,"Password cannot be empty");
                    LoginDialog.this.passwordField.requestFocus();
                    return;
                }
                disableAll();
                loginDialogEventHandler.onLoginButtonClicked(username,password);
            }
        });
    }
    private void disableAll()
    {
        this.usernameTextField.setEnabled(false);
        this.passwordField.setEnabled(false);
        this.loginButton.setEnabled(false);
    }
    public void enableAll()
    {
        this.usernameTextField.setEnabled(true);
        this.passwordField.setEnabled(true);
        this.loginButton.setEnabled(true);
    }
    public void clear()
    {
        this.usernameTextField.setText("");
        this.passwordField.setText("");
        enableAll();
    }
    public void displayErrorMessage(String error)
    {
        JOptionPane.showMessageDialog(this,error);
    }
}

