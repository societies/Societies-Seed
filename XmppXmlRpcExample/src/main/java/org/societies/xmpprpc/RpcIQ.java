package org.societies.xmpprpc;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.StringUtils;

public class RpcIQ extends IQ {
	
	private String thePayload = "";
	
	public String toString()
	{
		return this.getFrom() + " : " +  thePayload;
	}
	public String getThePayload() {
		return thePayload;
	}




	public void setThePayload(String thePayload) {
		this.thePayload = thePayload;
	}




	@Override
	public String getChildElementXML() {
		StringBuilder buf = new StringBuilder();
        buf.append("<query xmlns=\"jabber:iq:rpc\">");
        // have to insert the rpc code here.
        buf.append(thePayload);
        buf.append("</query>");
        return buf.toString();
		
	}

}
