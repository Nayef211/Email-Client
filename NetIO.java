import java.io.*;
import java.net.*;

public class NetIO {
    public static final int BUFFER_SIZE = 2048;
    
    public static String myIPAddress() {
	String ipAddress = "";
	try {
	    InetAddress me = InetAddress.getLocalHost ();
	    ipAddress = me.getHostAddress();
	}
	catch(Exception e) {
	    e.printStackTrace ();
	}
	return ipAddress;
    }

    public static String myUserName() {
	String userName = "";
	try {
	    InetAddress me = InetAddress.getLocalHost ();
	    userName = System.getProperty("user.name");
	}
	catch(Exception e) {
	    e.printStackTrace ();
	}
	return userName;
    }
	
    public static int sendRequest (String message, String destinationIPAddress) {        
	int errorCode = Globals.NET_SEND_ERROR; 
	int attempts  = 0;
	do {
	    try {
		// create socket and timeout
		Socket me = new Socket();
		me.connect(new InetSocketAddress(destinationIPAddress, Globals.PORT_NUMBER), Globals.TIME_OUT);
		me.setSoTimeout(Globals.TIME_OUT);

		// get output stream and send request
		DataOutputStream output = new DataOutputStream (me.getOutputStream());
		output.writeUTF(message); 

		// wait for confirmation code from receiving node 
		DataInputStream input = new DataInputStream(me.getInputStream());
		String request = input.readUTF();
		if (isANumber(request))
		    errorCode = Integer.parseInt(request);

		me.close ();
	    }
	    catch (IOException e) {
		if (Globals.DEBUG_ON) {
		    System.out.println("Exception thrown in sendRequest");
		    e.printStackTrace ();
		}  
	    }                  
	    attempts++;
	} while (errorCode != Globals.NET_OK && attempts < Globals.SENDING_ATTEMPTS_LIMIT);
	
	if (Globals.DEBUG_ON)
	    System.out.println("errorCode=" + errorCode + " attempts=" + attempts);
	
	return errorCode;
    }
    
    public static String receiveRequest (){
	String request = Globals.STR_NULL;
	int errorCode = Globals.NET_RECEIVE_ERROR;
	try {
	    // create server socket
	    ServerSocket server = new ServerSocket(Globals.PORT_NUMBER, 100);
	    
	    // create socket and timeout
	    Socket me = server.accept();
	    me.setSoTimeout(Globals.TIME_OUT);
		    
	    // get input stream and receive request
	    DataInputStream input = new DataInputStream(me.getInputStream());
	    request = input.readUTF();
	    
	    // get client's ip-address
	    Globals.clientIPAddress = me.getInetAddress().getHostAddress();
	    
	    // get output stream and send confirmation
	    DataOutputStream output = new DataOutputStream(me.getOutputStream());
	    output.writeUTF(Globals.STR_NULL + Globals.NET_OK); 

	    me.close ();               
	    server.close ();
	    errorCode = Globals.NET_OK;
	}
	catch (IOException e) {
	    if (Globals.DEBUG_ON) {            
		System.out.println("Exception thrown in receiveRequest");
		e.printStackTrace ();
	    }
	}
	return request;
    }
/*    
    public static void addToTransmissionString(int recordNumber) {
	Message message = new Message();
	message.readFromMessagesFile(recordNumber);
	Globals.transmissionString = Globals.transmissionString + message.getAsString() + Utils.intToBytesStr(Globals.END_OF_MESSAGE);
    }
    
*/    
 /*   
    private void splitRequest(String request, NodeInfo info) {        
	if (request.length() >= Constants.MINIMUM_MESSAGE_LENGTH) {
	    info.setCommand(request.substring(0, Constants.COMMAND_LENGTH));
	    info.setRowColPlayer(request.substring(Constants.COMMAND_LENGTH, Constants.COMMAND_LENGTH + Constants.ROW_COL_LENGTH));
	    info.setUserName(request.substring(Constants.COMMAND_LENGTH + Constants.ROW_COL_LENGTH, 
					       Constants.COMMAND_LENGTH + Constants.ROW_COL_LENGTH + Constants.CLIENT_ID_LENGTH));
	    
	    if (request.length() > Constants.MINIMUM_MESSAGE_LENGTH) {
		info.setMessage(request.substring(Constants.COMMAND_LENGTH + Constants.ROW_COL_LENGTH + Constants.CLIENT_ID_LENGTH));
	    }
	}
	else {
	    info.setCommand(Constants.NULL_STR + Constants.REQUEST_UNKNOWN);
	    info.setRowColPlayer(Constants.DEFAULT_ROW_COL);
	    info.setUserName(Constants.UNKNOWN_PLAYER_NAME);
	    info.setMessage(request);
	}
    }
   
    // This method puts the message sent by the source in the object info
    // It also puts the address of the source in the object info
    // The method returns an error code 
    // receiveRequest overload. Receives the information as
    // a String without splitting it into a NodeInfo
    
    public int receiveRequest (NodeInfo info) {
	int errorCode = Constants.NET_RECEIVE_ERROR;
	try {
	    // create server socket
	    ServerSocket server = new ServerSocket (Constants.PORT_NUMBER, 100);
	    
	    // create socket and timeout
	    Socket me = server.accept ();
	    me.setSoTimeout(Constants.TIME_OUT);
	     
	    // get input stream and receive request
	    DataInputStream input = new DataInputStream (me.getInputStream ());
	    String request = input.readUTF();
	    
	    // get output stream and send confirmation
	    DataOutputStream output = new DataOutputStream (me.getOutputStream ());
	    output.writeUTF (Constants.NULL_STR + Constants.NET_OK); 

	    info.setIPAddress(me.getInetAddress().getHostAddress());
	    splitRequest(request, info);
	    
	    me.close ();               
	    server.close ();
	    errorCode = Constants.NET_OK;
	}
	catch (IOException e) {
	    if (Constants.DEBUG_ON) {            
		System.out.println("Exception thrown in receiveRequest");
		e.printStackTrace ();
	    }
	}
	return errorCode;
    }   
    
    public static int sendFile(String fileName, String destinationIPAddress) {
	Socket me;  
	DataOutputStream output;
	RandomAccessFile f;
	byte[] buffer = new byte[BUFFER_SIZE];
	int errorCode = Constants.NET_TIME_OUT_ERROR;

	try {
	    f = new RandomAccessFile(fileName, "r");

	    try {
		me = new Socket (destinationIPAddress, Constants.PORT_NUMBER);
		output = new DataOutputStream (me.getOutputStream ());
		output.writeUTF(fileName);
		
		int bytes = f.read(buffer);
		while (bytes != -1) {
		    output.write(buffer, 0, bytes);
		    bytes = f.read(buffer);   
		}
		me.close();
		errorCode = Constants.NET_OK;
	    }
	    catch(IOException e) {
		if (Constants.DEBUG_ON) 
		    System.out.println("Error sending " + fileName + " in sendFile");
		errorCode = Constants.NET_SEND_ERROR;
	    }
	    f.close();
	}
	catch(IOException e) {
	    if (Constants.DEBUG_ON)
		System.out.println("Error opening " + fileName + " in sendFile");
	    errorCode = Constants.FILE_OPEN_ERROR;
	}
	
	return errorCode;
    }
    
    public int receiveFile(NodeInfo info) {
	ServerSocket me;
	Socket connection;
	RandomAccessFile f;
	String fileName = Constants.NULL_STR;
	byte[] buffer = new byte[BUFFER_SIZE];
	int errorCode = Constants.NET_OK;
	
	try {
	    me = new ServerSocket (Constants.PORT_NUMBER, 100);
	    if (Constants.DEBUG_ON)
		System.out.print("waiting for file...");

	    me.setSoTimeout(Constants.FILE_TRANSFER_TIME_OUT);
	    connection = me.accept (); 
	    info.setIPAddress(connection.getInetAddress().getHostAddress());

	    DataInputStream input = new DataInputStream (connection.getInputStream ());
	    input = new DataInputStream (connection.getInputStream ());
	    
	    info.setMessage(input.readUTF()); // contains filename
	    fileName = info.getMessage();
	    
	    try {
		f = new RandomAccessFile(fileName, "rw");
		f.setLength(0);

		if (Constants.DEBUG_ON)
		    System.out.print(fileName + " from " + info.getIPAddress() + " in process...");

		int bytes = input.read(buffer);
		while (bytes != -1) {
		    f.write(buffer, 0, bytes);
		    bytes = input.read(buffer);  
		}
		f.close();
		if (Constants.DEBUG_ON)
		    System.out.println("done");
	    }
	    catch(IOException e) {
		if (Constants.DEBUG_ON) 
		    System.out.println("Error opening " + fileName + " in receiveFile");
		errorCode = Constants.FILE_CREATE_ERROR;
	    }
	    me.close();
	}
	catch(IOException e) {
	    if (Constants.DEBUG_ON)
		System.out.println("Error connecting/receiving in receiveFile");
	    errorCode = Constants.NET_TIME_OUT_ERROR;
	}
	return errorCode;
    }
 */
    private static boolean isANumber (String s) {
	boolean result = true;
	for (int i = 0 ; i < s.length () ; i++)
	    result = result && Character.isDigit(s.charAt (i));
	return result;
    }
}

