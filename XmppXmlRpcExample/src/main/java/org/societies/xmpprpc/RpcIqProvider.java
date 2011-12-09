package org.societies.xmpprpc;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class RpcIqProvider implements IQProvider {
	/**
	 * Parses an IQ packet.
	 * 
	 * @param parser
	 *            the XML parser, positioned at the start of an IQ packet.
	 * @return an IQ object.
	 * @throws Exception
	 *             if an exception occurs while parsing the packet.
	 */
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		RpcIQ iqPacket = new RpcIQ();
		boolean done = false;
		StringBuffer mySb = new StringBuffer();
		while (!done) {
			int eventType = parser.next();
			String elementName = parser.getName();

			if (eventType == XmlPullParser.START_TAG) {				
				elementName = parser.getName();
				mySb.append("<"+elementName);
				int myAttCount = parser.getAttributeCount();
				if(myAttCount!=0){
					for(int i=0;i<myAttCount;i++){
						mySb.append(" ").append(parser.getAttributeName(i));
						mySb.append("=\"").append(parser.getAttributeValue(i));
						mySb.append("\"");
					}
				}				
				mySb.append(">");
			} else if (eventType == XmlPullParser.TEXT) {
				mySb.append(parser.getText());
			} else if (eventType == XmlPullParser.END_TAG) {
				elementName = parser.getName();
				if (elementName.equals("query")) {
					iqPacket.setThePayload(mySb.toString());
					done = true;
				}
				else {
					// append the close tags. 
					mySb.append("</"+elementName+">");
				}
			}
		}
		return iqPacket; 
	}

}

