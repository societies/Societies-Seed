package org.societies.xmpprpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientException;
import org.apache.xmlrpc.client.XmlRpcStreamTransport;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.apache.xmlrpc.client.XmlRpcTransportFactoryImpl;
import org.apache.xmlrpc.common.XmlRpcStreamRequestConfig;
import org.jivesoftware.smack.packet.IQ.Type;
import org.xml.sax.SAXException;

public class XmlRpcByteArrayClient extends XmlRpcStreamTransport {

	protected XmlRpcByteArrayClient(XmlRpcClient client, XmppXmlRpcClientServer aParent, String anEndpoint) {
		super(client);
		theParent = aParent; 
		theOutByteStream = new ByteArrayOutputStream();
		theEndpoint = anEndpoint; 
	}
	private ByteArrayInputStream theInByteStream;
	private ByteArrayOutputStream theOutByteStream;
	private XmppXmlRpcClientServer theParent; 
	private String theId = null; 
	private String theEndpoint; 
	
	protected static class ByteArrayClientTransportFactory extends XmlRpcTransportFactoryImpl
	{
		XmppXmlRpcClientServer theParent; 
		String theJid; 
		public ByteArrayClientTransportFactory(XmlRpcClient aClient, XmppXmlRpcClientServer aParent, String aJid)
		{
			super(aClient);
			theParent = aParent; 
			theJid = aJid; 
		}
		
		@Override
		public XmlRpcTransport getTransport() {
			return new XmlRpcByteArrayClient(getClient(), theParent, theJid);
		}
		
	}

	@Override
	protected void close() throws XmlRpcClientException {
		//System.out.println("Close ...");
	}
	@Override
	protected InputStream getInputStream() throws XmlRpcException {
		System.out.println("Getting input stream ...");
		// have to wait for the response return ... must be a blocking call ... 
		try{
			String myResponse = theParent.getTheRpcResponseQueue(theId).poll(300, TimeUnit.SECONDS);
			theInByteStream = new ByteArrayInputStream(myResponse.getBytes());
			return theInByteStream;
		}
		catch(InterruptedException anEx){
			throw new XmlRpcException(anEx.getMessage());
		}
	}
	@Override
	protected boolean isResponseGzipCompressed(XmlRpcStreamRequestConfig config) {
		return false;
	}
	@Override
	protected void writeRequest(ReqWriter writer) throws XmlRpcException,
			IOException, SAXException {
		writer.write(theOutByteStream);
		String myRequest = theOutByteStream.toString();
		// have to remove a potential xml preamble.
		if (myRequest.indexOf("?>") != 0) {
			myRequest = myRequest
					.substring(myRequest.indexOf("?>") + 2);
		}
		
		// have to wrap the bytes into an IQ packet and deliver it ...
		theId = UniqueIdGenerator.getUniqueId();
		RpcIQ myIq = new RpcIQ();
		myIq.setTo(theEndpoint);
		myIq.setPacketID(theId);
		myIq.setThePayload(myRequest);
		myIq.setFrom(theParent.getMyJid());
		myIq.setType(Type.SET);
		theParent.getConnection().sendPacket(myIq);
	}

}
