import java.io.*;
public class Globals {
    // debug constants
    public static final boolean DEBUG_ON = false; 

    // initialization constants
    public static final int NULL = 0;
    public static final String STR_NULL = "";
    public static final char BLANK = ' ';
    public static final char CR = '\n';
    public static final int LENGTH_OF_INT = 4;

    // error constants
    public static final int PROCESS_OK = 0;
    public static final int PROCESS_ERROR = -1;
    
    // position constants for message
    // Structure of message:
    // command + sender + receiver + timeStamp + first record marker + subject + end of subject marker + message text
       
    public static final int COMMAND_POS = 0;
    public static final int COMMAND_LEN = 1;
    
    public static final int CLIENT_ID_LEN = 9;                          // these two are here separately because sometimes the client's request contains
    public static final int CLIENT_POS = COMMAND_POS + COMMAND_LEN;     // only C + sender or C + receiver, in which case the position of sender and receiver are equal
    
    public static final int SENDER_POS  = COMMAND_POS + COMMAND_LEN;
    public static final int SENDER_LEN  = CLIENT_ID_LEN;
    public static final int RECEIVER_POS = SENDER_POS + SENDER_LEN;
    public static final int RECEIVER_LEN  = CLIENT_ID_LEN;
    public static final int DATE_TIME_POS = RECEIVER_POS + RECEIVER_LEN;
    public static final int DATE_TIME_LEN = 8;  // long current milliseconds coded as eight bytes
    public static final int MARKER_POS = DATE_TIME_POS + DATE_TIME_LEN;
    public static final int MARKER_LEN = 1;
    public static final char END_OF_SUBJECT_MARKER = '~';
    public static final int END_OF_SUBJECT_MARKER_LEN = 1;
    public static final int IDENTIFICATION_LEN = SENDER_LEN + RECEIVER_LEN + DATE_TIME_LEN;
						 
    // constants for records; note that the first record of a message will contain the
    // identification and marker; therefore, there will be more room for text in the
    // subsequent records
    
    public static final int TEXT_LEN = END_OF_SUBJECT_MARKER_LEN + 30;
    public static final int RECORD_DATA_LEN = COMMAND_LEN   + 
					      SENDER_LEN    +
					      RECEIVER_LEN  +
					      DATE_TIME_LEN +
					      MARKER_LEN    +
					      TEXT_LEN;
    
    public static final int NEXT_RECORD_LEN = 4;  // integer that stores pointer to next record
    public static final int RECORD_LEN = RECORD_DATA_LEN + NEXT_RECORD_LEN;  // integer at end: 40 bytes
    public static final int END_OF_MESSAGE = -1;                             // marks end of list of records that make up a message
    
    // message and record delimiters; characters used so that special ascii like 1, 2, 3, can be used    
    public static final char FIRST_RECORD_OF_MESSAGE = '+';       // we mark the start of a message with this marker in case we have to rebuild the indices
    public static final char DELETED = '*';                // this character will only be placed at front of every record in case
							   // we need to rebuild the deleted linked list 
    
    // constants for linked list of available records
    public static int AVAILABLE_LIST_IS_EMPTY = -1; // no records deleted.

    // global variables
    public static RandomAccessFile msg = null;       // main messages file
    //public static AvailableList availableList = new AvailableList();    // start of available of records linked list
    public static int totalRecordsInMessageFile = 0; // update every time a record is added; fileLen does not update fast enough  
    
    public static final int APPEND = 1;  // modes for writing into the file
    public static final int MODIFY = 2; 
    
    // messages file
    public static final String MESSAGES_FILE = "_messages.txt";
    
    // available list file
    public static final String AVAILABLE_LIST_FILE = "_available.txt";
    public static final int AVAILABLE_NODE_RECORD_NUMBER_LEN = 4;
    
    // accounts file
    public static final String ACCOUNTS_FILE = "_accounts.txt";
    public static String[] accounts = null;
    
    // binary tree file
    public static final String SENDER_TREE_FILE = "_stree.txt";  // tree by sender
    public static final String RECIPIENT_TREE_FILE = "_rtree.txt";  // tree by recipient
    
    public static final int KEY_LEN = SENDER_LEN + RECEIVER_LEN + DATE_TIME_LEN;
    
    public static final int SENDER_ID   = 0;
    public static final int RECEIVER_ID = 1;
    
    // Constants for client
    public static final int MAX_CLIENT_MESSAGES = 500;
    public static final String EMPTY_CLIENT_MESSAGE = STR_NULL; 
    
    // Global variables for client
    public static String[] boxMessages = new String[MAX_CLIENT_MESSAGES];
    public static String clientIPAddress = STR_NULL;        // server needs this to know who sent request
    
    // Server command constants
    public static final char SEND_MESSAGE    = 'S';            // client has sent a message
    public static final char IN_BOX          = 'I';            // client wants to retrieve their own mail
    public static final char OUT_BOX         = 'O';            // client wants to retrieve mail they have sent
    public static final char SERVER_SHUTDOWN = 'Q';
    
    // Network constants
    public static final String SERVER_IP_ADDRESS = "10.104.82.64";
    public static final int PORT_NUMBER = 5000;
    public static final int TIME_OUT    = 10000;   // send request with a 10000 millisecond timeout
    
    public static final int NET_OK            = 0;
    public static final int NET_SEND_ERROR    = -1;
    public static final int NET_RECEIVE_ERROR = -2;  
    
    public static final int SENDING_ATTEMPTS_LIMIT = 1000;
    public static final int END_OF_MESSAGES_TRANSMISSION = -2;
    
    // Global network variables
    public static String transmissionString = STR_NULL; // concatenates all messages into a single string. then it's decomposed in the client
}
