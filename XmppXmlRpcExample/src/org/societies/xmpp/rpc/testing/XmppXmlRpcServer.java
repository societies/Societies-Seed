package org.societies.xmpp.rpc.testing;

import org.societies.xmpprpc.XmppXmlRpcClientServer;
import org.societies.xmpprpc.examplehandler.XmlRpcExampleHandler;

public class XmppXmlRpcServer {

	/**
	 * @param args
	 */

	public static void initializeRpc() throws Exception {

		System.out.println("Connecting ... ");
		String myResource = "ChartWindow";
		System.out.println("Assigned ressource id " + myResource);
		XmppXmlRpcClientServer theXmppRpc = new XmppXmlRpcClientServer(
				"green", "red.local", "green", myResource);
		theXmppRpc.connect();
		Thread t = new Thread(theXmppRpc);
		t.start();
		while (!theXmppRpc.getConnection().isAuthenticated()) {
			Thread.sleep(100);
		}
		System.out.println("Connected and authenticated ... ");
		// add rpc handlers.
		// now on to the xml rpc part ...
		// add an xml rpc handler class ...
		// (the server part, it can be called from any node on the network)
		theXmppRpc.getTheXmlRpcHandlers().addHandler("window", new LocalDelegator());
		//theXmppRpc.getTheXmlRpcHandlers().addHandler("example", XmlRpcExampleHandler.class);
		theXmppRpc.getTheXmlRpcHandlers().addHandler("examples",  new XmlRpcExampleHandler());

	}

	public static void main(String[] args) {

		try {
			XmppXmlRpcServer.initializeRpc();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
