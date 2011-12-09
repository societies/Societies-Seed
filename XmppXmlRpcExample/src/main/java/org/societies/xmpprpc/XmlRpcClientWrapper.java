package org.societies.xmpprpc;

import java.util.List;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.societies.xmpprpc.XmlRpcByteArrayClient.ByteArrayClientTransportFactory;

public class XmlRpcClientWrapper {

	private XmppXmlRpcClientServer theParent;
	private XmlRpcClient theClient;
	private String theEndpoint; 

	public XmlRpcClientWrapper(XmppXmlRpcClientServer aParent, String aRpcEndpointJid) {

		theParent = aParent;
		theEndpoint = aRpcEndpointJid; 
		
		theClient = new XmlRpcClient();
		// set the transport factory on the client
		theClient.setTransportFactory(new ByteArrayClientTransportFactory(theClient, theParent, theEndpoint));
	}
	
	/**
	 * this method must be synchronized because of the underlying asynchronous ID generation. 
	 * In case it is requried to send multiple rpc requests at the same time, instantiate 
	 * multiple xml rpc client wrappers. 
	 * @param aMethod
	 * @param aParamList
	 * @return
	 * @throws XmlRpcException
	 */
	public synchronized 
	Object execute(String aMethod, List<Object> aParamList) throws XmlRpcException
	{
		return theClient.execute(aMethod, aParamList);
	}

	
	
	
}
