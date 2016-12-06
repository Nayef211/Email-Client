import javax.swing.*;
import java.awt.Dimension;
import javax.swing.event.*;
import java.util.Date;

public class EmailClientPane extends JPanel implements ListSelectionListener {
    private String[] messageHeaders = new String[Globals.MAX_CLIENT_MESSAGES];
    private String messageText = "";
    
    public JList messageHeadersJList = null;
    private JTextArea messageJTextArea = null;
    
    private JScrollPane messageHeadersJScrollPane = null;
    private JScrollPane messageTextJScrollPane = null;

    private JSplitPane splitPane = null;
    
    public EmailClientPane() {
	initializeMessageHeaders();
	
	//Create the list of images and put it in a scroll pane.
	messageHeadersJList = new JList(messageHeaders);
	messageHeadersJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	messageHeadersJList.setSelectedIndex(0);
    
	messageHeadersJList.addListSelectionListener(this);
	messageHeadersJScrollPane = new JScrollPane(messageHeadersJList);
    

	messageText = Globals.boxMessages[0].substring(Globals.boxMessages[0].indexOf(Globals.END_OF_SUBJECT_MARKER) + 1);

	messageJTextArea = new JTextArea(messageText);
	messageJTextArea.setLineWrap(true);
	messageJTextArea.setWrapStyleWord(true);
	messageJTextArea.setEditable(false);
	messageTextJScrollPane = new JScrollPane(messageJTextArea);
    
	//Create a split pane with the two scroll panes in it.
	splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, messageHeadersJScrollPane, messageTextJScrollPane);
	splitPane.setOneTouchExpandable(true);
	splitPane.setDividerLocation(250);
    
	//Provide minimum sizes for the two components in the split pane.
	Dimension minimumSize = new Dimension(800, 50);
	messageHeadersJScrollPane.setMinimumSize(minimumSize);
	messageTextJScrollPane.setMinimumSize(minimumSize);
    
	//Provide a preferred size for the split pane.
	splitPane.setPreferredSize(new Dimension(800, 600));
    }
    
    // initialize to From       Subject             Date Received     
    public void initializeMessageHeaders() {
	for (int i = 0; i < Globals.boxMessages.length; i++) {
	    if (!Globals.boxMessages[i].equals(Globals.EMPTY_CLIENT_MESSAGE)) {
		String dateTime = Globals.STR_NULL;
		dateTime = Globals.boxMessages[i].substring(Globals.DATE_TIME_POS, Globals.MARKER_POS);
		Date date = new Date(Utils.bytesStrToLong(dateTime));
		dateTime = date.toString();
		
		messageHeaders[i] = Globals.boxMessages[i].substring(Globals.SENDER_POS, Globals.RECEIVER_POS) + "       " +
				    dateTime + "       " +
				    Globals.boxMessages[i].substring(Globals.MARKER_POS + 1, Globals.boxMessages[i].indexOf(Globals.END_OF_SUBJECT_MARKER));
	    }
	    else
		messageHeaders[i] = Globals.EMPTY_CLIENT_MESSAGE;
	}
    }
    
    public void valueChanged(ListSelectionEvent e) {
// there is a bug here: when the message is empty, the substring returns -1, then +1 is added and so an empty string is returned

	int index = messageHeadersJList.getSelectedIndex();
	messageText = Globals.boxMessages[index].substring(Globals.boxMessages[index].indexOf(Globals.END_OF_SUBJECT_MARKER) + 1);
	messageJTextArea.setText(messageText);

    } 
    
    public JSplitPane getSplitPane() {
	return splitPane;
    }
   
    public String getMessageText() {
	return messageText;
    }
}
