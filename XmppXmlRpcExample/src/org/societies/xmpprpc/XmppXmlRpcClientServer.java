package org.societies.xmpprpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.provider.ProviderManager;
import org.societies.xmpprpc.examplehandler.XmlRpcExampleHandler;

public class XmppXmlRpcClientServer implements Runnable {

	private String theUsername = "red";
	private String thePassword = "red";
	private String theServer = "red.local";
	private String theResource = "rpc";

	private XmlRpcByteArrayServer theRpcServer = new XmlRpcByteArrayServer();
	private XmlRpcClientWrapper theRpcClient;

	private XMPPConnection connection;

	private InstanceBasedHandler theXmlRpcHandlers; 
	
	public InstanceBasedHandler getTheXmlRpcHandlers() {
		return theXmlRpcHandlers;
	}

	public void setTheXmlRpcHandlers(InstanceBasedHandler theXmlRpcHandlers) {
		this.theXmlRpcHandlers = theXmlRpcHandlers;
	}

	public XMPPConnection getConnection() {
		return connection;
	}

	private final Hashtable<String, BlockingQueue<String>> theRpcRequests = new Hashtable<String, BlockingQueue<String>>();

	public Hashtable<String, BlockingQueue<String>> getTheRpcRequests() {
		return theRpcRequests;
	}

	public BlockingQueue<String> getTheRpcResponseQueue(String aUniqueId) {
		if (theRpcRequests.get(aUniqueId) == null) {
			BlockingQueue<String> myQueue = new SynchronousQueue<String>();
			theRpcRequests.put(aUniqueId, myQueue);
		}
		return theRpcRequests.get(aUniqueId);
	}

	public XmlRpcByteArrayServer getTheRpcServer() {
		return theRpcServer;
	}

	public void setTheRpcServer(XmlRpcByteArrayServer theRpcServer) {
		this.theRpcServer = theRpcServer;
	}

	public XmlRpcClientWrapper getTheRpcClient() {
		return theRpcClient;
	}

	public XmppXmlRpcClientServer(String aUsername, String aServer, String aPassword, String aResource) throws Exception {
		theUsername = aUsername; 
		theServer = aServer;
		thePassword = aPassword; 
		theResource = aResource;
		initializeRpc();
	}

	public String getMyJid() {
		return theUsername + "@" + theServer + "/" + theResource;
	}

	/**
	 * override this in your own implementation
	 * 
	 * @throws Exception
	 */
	public void initializeRpc() throws Exception {

		// set some handlers ...
		theXmlRpcHandlers = new InstanceBasedHandler();
		
		// set the handlers.
		theRpcServer.setHandlerMapping(theXmlRpcHandlers);
		
		

		// initialize the client
		//theRpcClient = new XmlRpcClientWrapper(this, "green@red.local/ChartWindow");
		theRpcClient = new XmlRpcClientWrapper(this, getMyJid());

	}

	/**
	 * connects to the jabber server, adds the rpc iq handler and listener.
	 * 
	 * @throws Exception
	 */
	public void connect() throws Exception {

		// register our own rpc iq handler.
		ProviderManager.getInstance().addIQProvider("query", "jabber:iq:rpc",
				new RpcIqProvider());

		ConnectionConfiguration config = new ConnectionConfiguration(theServer,
				5222);

		connection = new XMPPConnection(config);
		connection.connect();
		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
		connection.login(theUsername, thePassword, theResource);
		// add a packet filter for the rpc code ...

		// listen for incoming rpc request.
		PacketFilter incomingIqSetFilter = new AndFilter(new PacketTypeFilter(
				IQ.class));

		PacketListener myListenerSet = new PacketListener() {
			public void processPacket(Packet packet) {
				// Do something with the incoming packet here.
				if (packet instanceof RpcIQ) {
					handleRpcPacket((RpcIQ) packet);
				}
			}
		};
		// Register the listener.
		connection.addPacketListener(myListenerSet, incomingIqSetFilter);

	}

	/**
	 * RPC SERVER is single threaded at the moment.
	 * 
	 * @param aPacket
	 */
	public void handleRpcPacket(RpcIQ aPacket) {
		try {
			// ok, we have receivedn an rpc packet, need to handle
			// it
			// based on type, etc.
			System.out.println("incoming rpc iq : " + aPacket);
			RpcIQ myRpcIq = (RpcIQ) aPacket;
			// work out sets.

			if (myRpcIq.getType() == Type.SET) {
				// work out an rpc request
				String myResponse = theRpcServer.execute(myRpcIq
						.getThePayload());
				// have to remove a potential xml preamble.
				if (myResponse.indexOf("?>") != 0) {
					myResponse = myResponse
							.substring(myResponse.indexOf("?>") + 2);
				}
				System.out.println(myResponse);
				myRpcIq.setThePayload(myResponse);
				myRpcIq.setType(Type.RESULT);
				String myFrom = myRpcIq.getFrom();
				myRpcIq.setFrom(myRpcIq.getTo());
				myRpcIq.setTo(myFrom);
				connection.sendPacket(myRpcIq);
			} else if (myRpcIq.getType() == Type.RESULT) {
				// get the id of the packet ...
				String myId = (String) aPacket.getPacketID();
				// get the corresponding client ... in case there is none,
				// ignore this packet.
				BlockingQueue<String> myOriginRequestQueue = getTheRpcResponseQueue(myId);
				myOriginRequestQueue.put(myRpcIq.getThePayload());
			} else if (myRpcIq.getType() == Type.ERROR) {
				// get the id of the packet ...
				String myId = (String) aPacket.getPacketID();
				// get the corresponding client ... in case there is none,
				// ignore this packet.
				// TODO: WRAP IT INTO AN XMLRPC ERROR RESPONSE ****** VERY
				// IMPORTANT *****
				BlockingQueue<String> myOriginRequestQueue = getTheRpcResponseQueue(myId);
				myOriginRequestQueue.put(myRpcIq.getThePayload());
			}
		} catch (XmlRpcException anEx) {
			anEx.printStackTrace();
		} catch (InterruptedException anEx) {
			anEx.printStackTrace();
		} catch (IOException anEx) {
			anEx.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		XmppXmlRpcClientServer myCS = new XmppXmlRpcClientServer("red", "red.local", "red", "rpc");
		myCS.connect();
		Thread t = new Thread(myCS);
		t.start();
		while (!myCS.connection.isAuthenticated()) {
			Thread.sleep(100);
		}
		// ok ... all xmpp stuff ready.
		System.out.println("READDYYYYYY");

		
		// now on to the xml rpc part ... 
		// add an xml rpc handler class ...(the server part, it can be called from any node on the network) 
		myCS.getTheXmlRpcHandlers().addHandler("examples", XmlRpcExampleHandler.class);

		// initialize an xmlrpc client that will send commands to a specific jid ... 
		XmlRpcClientWrapper myWrapper = new XmlRpcClientWrapper(myCS,
				"red@red.local/rpc");

		for (int i = 0; i < 100; i++) {

			// call a simple sample method, in this case, of ourself, but over xmlrpc over the network. 
			long myT1 = System.currentTimeMillis();
			Object myRet = myWrapper.execute("examples.getRandomQuote",
					new ArrayList<Object>());
			long myT2 = System.currentTimeMillis();
			System.out.println("Got : " + myRet.toString());
			System.out.println("This took : " + (myT2 - myT1));
			Thread.sleep(1000);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (theFlag) {
			try {
				Thread.sleep(100);
			} catch (Exception anEx) {
				anEx.printStackTrace();
			}
		}
	}

	private boolean theFlag = true;

}
