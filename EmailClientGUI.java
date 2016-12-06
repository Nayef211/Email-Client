import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EmailClientGUI implements ActionListener {
    private JFrame frame = null;
    private JPanel panel1 = null;
    private JPanel panel2 = null;
    private JPanel panel3 = null;
    
    private JLabel titlesLabel = null;
    
    private EmailClientPane eMailClientPane = null;
    
    JButton compose = null;
    JButton getMail = null;
    JButton delete  = null;
    JButton inBox   = null;
    JButton outBox  = null;
    JButton serverShutdown = null;

    public EmailClientGUI() {	
	frame = new JFrame("Bloor CI Email Client Version 2016.0");
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	frame.setLocation(100, 50);
        frame.setResizable(false);
	// frame.addWindowListener(new WindowEventHandler());

        Container contentPane = frame.getContentPane();
        BoxLayout contentPaneLayout = new BoxLayout(contentPane, BoxLayout.Y_AXIS);
        contentPane.setLayout(contentPaneLayout);
        
        panel1 = new JPanel();
        panel2 = new JPanel();
	panel3 = new JPanel();
        
        contentPane.add(panel1);
        contentPane.add(panel2);
	contentPane.add(panel3);
        
        // set up the first panel
        FlowLayout panel1Layout = new FlowLayout(FlowLayout.LEFT);
        panel1.setLayout(panel1Layout);
        
        titlesLabel = new JLabel(" From                  Date Received                                        Subject");
        panel1.add(titlesLabel);
        
        // set up the second panel
        FlowLayout panel2Layout = new FlowLayout(FlowLayout.LEFT);
        panel2.setLayout(panel2Layout);
        eMailClientPane = new EmailClientPane();
	panel2.add(eMailClientPane.getSplitPane());

        // set up the third panel
        FlowLayout panel3Layout = new FlowLayout(FlowLayout.CENTER);
        panel3.setLayout(panel3Layout);
        panel3.setPreferredSize(new Dimension(800, 40));

 	compose = new JButton("Compose");
	getMail = new JButton("Get Mail");
	delete  = new JButton("Delete");
	inBox   = new JButton("InBox");
	outBox  = new JButton("OutBox");
        //serverShutdown = new JButton("Server Shutdown");

        compose.addActionListener(this);
        getMail.addActionListener(this);
        delete.addActionListener(this);
        inBox.addActionListener(this);
        outBox.addActionListener(this);
        //serverShutdown.addActionListener(this);
        
        panel3.add(compose);
        panel3.add(getMail);
	panel3.add(delete);
	panel3.add(inBox);
	panel3.add(outBox);
        //panel3.add(serverShutdown);
        
	frame.pack();
	frame.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent event) {
        Object buttonPressed = event.getSource();
        
        if (buttonPressed == compose) {
            EmailClientComposeMessage c = new EmailClientComposeMessage();
        }
        else if (buttonPressed == getMail) {
            System.out.println("Get Mail");        
        }
        else if (buttonPressed == delete) {
            System.out.println("Delete");
        }
        else if (buttonPressed == inBox) {
            for (int i = 0; i < Globals.boxMessages.length; i++) {
                Globals.boxMessages[i] = Globals.EMPTY_CLIENT_MESSAGE; //Globals.STR_NULL;
            }
            
            int error = MailTransfers.EmailClientRequestAllMail(Globals.RECEIVER_ID);
            if (error == Globals.PROCESS_OK) {
                panel2.remove(eMailClientPane.getSplitPane());
                eMailClientPane = new EmailClientPane();
                panel2.add(eMailClientPane.getSplitPane());
                frame.pack();
                frame.setVisible(true); 
            }
            else if (Globals.DEBUG_ON) {
                System.out.println("Error loading boxMessages: Globals.boxMessages[] is now not consistent with display");
            }
        }
        else if (buttonPressed == outBox) {
            for (int i = 0; i < Globals.boxMessages.length; i++) {
                Globals.boxMessages[i] = Globals.EMPTY_CLIENT_MESSAGE; //Globals.STR_NULL;
            }
            
            int error = MailTransfers.EmailClientRequestAllMail(Globals.SENDER_ID);
            if (error == Globals.PROCESS_OK) {
                panel2.remove(eMailClientPane.getSplitPane());
                eMailClientPane = new EmailClientPane();
                panel2.add(eMailClientPane.getSplitPane());
                frame.pack();
                frame.setVisible(true); 
            }
            else if (Globals.DEBUG_ON) {
                System.out.println("Error loading boxMessages: Globals.boxMessages[] is now not consistent with display");
            }
        }
        else if (buttonPressed == serverShutdown) {
            System.out.println("Server shutdown in process...");
            int errorCode = NetIO.sendRequest("" + Globals.SERVER_SHUTDOWN, Globals.SERVER_IP_ADDRESS);
            if (errorCode == 0) {
                System.out.println("...successful. All files closed.");
                frame.dispose();
            }
            else {
                System.out.println("...error in transmission. Not able to shutdown server. Client still running");
            }
        }
    }
    
    public static void main(String[] args) {
        EmailClientGUI gui = new EmailClientGUI();
    }
    
}
