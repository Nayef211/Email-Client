import javax.swing.JOptionPane;

public class MailTransfers {
    public static int EmailClientRequestAllMail(int whatId) {
	int error = Globals.NET_RECEIVE_ERROR;
	String identification = NetIO.myUserName();
	identification = identification.length() == 0 ? Globals.STR_NULL : Utils.leftPad(identification, Globals.CLIENT_ID_LEN, '0');
	if (identification.length() == Globals.CLIENT_ID_LEN) {
	    do {
		if (whatId == Globals.RECEIVER_ID) 
		    error = NetIO.sendRequest("" + Globals.IN_BOX + identification, Globals.SERVER_IP_ADDRESS);
		else 
		    error = NetIO.sendRequest("" + Globals.OUT_BOX + identification, Globals.SERVER_IP_ADDRESS);

		if (error == Globals.NET_OK) {
		    String boxMessages = NetIO.receiveRequest(); // when an I request is sent, the server joins all boxMessages into a single string for transmission
		    int i = 0;
		    while (!boxMessages.equals(Utils.intToBytesStr(Globals.END_OF_MESSAGES_TRANSMISSION)) && i < Globals.MAX_CLIENT_MESSAGES) {
			Globals.boxMessages[i] = boxMessages.substring(0, boxMessages.indexOf(Utils.intToBytesStr(Globals.END_OF_MESSAGE)));
			boxMessages = boxMessages.substring(boxMessages.indexOf(Utils.intToBytesStr(Globals.END_OF_MESSAGE)) + Globals.LENGTH_OF_INT);
			i++;
		    } 
		}
		else if (Globals.DEBUG_ON) {
		    System.out.println("***Error: Unable to receive boxMessages");
		}
		Utils.delay(500);
	    } while (error != Globals.NET_OK);
	}
	else {
	    JOptionPane.showMessageDialog (null, 
					   "Request identification must be 9 characters long: Cannot load boxMessages",
					   "ICS Bloor CI", 
					   JOptionPane.ERROR_MESSAGE);
	}
	return error;
    }    
}
