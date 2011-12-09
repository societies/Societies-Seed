package org.societies.xmpprpc;

public class UniqueIdGenerator {

	public static String getUniqueId()
	{
		long myMs = System.currentTimeMillis();
		if(myMs == theLastMs)theIndex++;
		else theIndex = 0; 
		String myId = theHost + "." + myMs+"."+theIndex;
		theLastMs = myMs; 
		return myId;
		
	}

	private static int theIndex = 0; 
	private static long theLastMs = 0L;
	private static String theHost = "";  

}
