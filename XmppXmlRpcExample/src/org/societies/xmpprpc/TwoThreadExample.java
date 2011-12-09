package org.societies.xmpprpc;

import java.util.ArrayList;

import org.societies.xmpprpc.examplehandler.XmlRpcExampleHandler;

/**
 * 
 * @author ustaudinger
 *
 */
public class TwoThreadExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Runnable r = new Runnable(){
			public void run(){
				try{
					// TODO Auto-generated method stub
					XmppXmlRpcClientServer myCS = new XmppXmlRpcClientServer("uls", "jabber.org", "abcd", "rpc");
					myCS.connect();
					Thread t = new Thread(myCS);
					t.start();
					while (!myCS.getConnection().isAuthenticated()) {
						Thread.sleep(100);
					}
					// ok ... all xmpp stuff ready.
					System.out.println("READDYYYYYY");

					
					// initialize an xmlrpc client that will send commands to a specific jid ... 
					XmlRpcClientWrapper myWrapper = new XmlRpcClientWrapper(myCS,
							"uls@jabber.org/rpcServer");

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
				catch(Exception x){x.printStackTrace();}
			}
		};
		
		Runnable r2 = new Runnable(){
			public void run(){
				try{
					// TODO Auto-generated method stub
					XmppXmlRpcClientServer myCS = new XmppXmlRpcClientServer("uls", "jabber.org", "abcd", "rpcServer");
					myCS.connect();
					Thread t = new Thread(myCS);
					t.start();
					while (!myCS.getConnection().isAuthenticated()) {
						Thread.sleep(100);
					}
					// ok ... all xmpp stuff ready.
					System.out.println("READDYYYYYY");

					// now on to the xml rpc part ... 
					// add an xml rpc handler class ...(the server part, it can be called from any node on the network) 
					myCS.getTheXmlRpcHandlers().addHandler("examples", XmlRpcExampleHandler.class);

				}
				catch(Exception x){x.printStackTrace();}
			}
		};
		
		Thread t1 = new Thread(r);
		Thread t2 = new Thread(r2);
		t1.start();
		t2.start();


	}

}
