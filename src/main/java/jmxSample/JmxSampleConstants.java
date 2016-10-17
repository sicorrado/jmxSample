package jmxSample;

public class JmxSampleConstants {
	
	//Global variable for sizing
	public static int DEFAULT_ARRAY_SIZE = 100000; 
	public static int DEFAULT_MIN_HEAP_SIZE = 100000000;
	
	public static String DEFAULT_URL = ":9999";
	
	//Global error messages
	public static String BAD_FORMATTED_URL_ERR = "Error - Bad formatted url : ";
	public static String BAD_FORMATTED_NAME_ERR = "Error - Bad formatted name";
	public static String CONNECTION_OPENING_ERR = "Error - JMX Connection refused or not available on url : ";
	public static String CONNECTION_CLOSING_ERR = "Error - JMX Connection not properly closed";
	public static String JMX_ERROR = "Error - JMX Operation in exception";
	
}
