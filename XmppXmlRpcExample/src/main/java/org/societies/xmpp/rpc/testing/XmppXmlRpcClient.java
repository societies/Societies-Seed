package org.societies.xmpp.rpc.testing;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.societies.xmpprpc.XmlRpcClientWrapper;
import org.societies.xmpprpc.XmppXmlRpcClientServer;

public class XmppXmlRpcClient {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws InterruptedException {
		XmppXmlRpcClientServer myCS = null;
		try {
			myCS = new XmppXmlRpcClientServer("red", "red.local", "red", "someReso");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  try {
			myCS.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  Thread t = new Thread(myCS);
		  t.start();
		  while (!myCS.getConnection().isAuthenticated()) {
		     try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		  // ok ... all xmpp stuff ready.
		  System.out.println("READDYYYYYY");
		 
		 // initialize an xmlrpc client that will send commands to a specific jid ... 
		 XmlRpcClientWrapper myWrapper = new XmlRpcClientWrapper(myCS, "green@red.local/ChartWindow");
		 
		 for (int i = 0; i < 5; i++) {
		   // call a simple sample method, in this case, of ourself, but over xmlrpc over the network. 
		   long myT1 = System.currentTimeMillis();
		   Object myRet = null;
		try {
			
			int para=1;
			@SuppressWarnings("rawtypes")
			Vector params = new Vector();
			params.addElement(new Integer(para));
			List paraObjList=new ArrayList<Object>();
			paraObjList.add(new Integer(para));			
//			myRet = myWrapper.execute("window.setData",paraObjList);
//			myRet = myWrapper.execute("window.setChartContainer",new ArrayList<Object>());
//		   	myRet = myWrapper.execute("examples.getRandomQuote",new ArrayList<Object>());
			myRet = myWrapper.execute("examples.getStateName",paraObjList);			
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   long myT2 = System.currentTimeMillis();
		   System.out.println("Got : " + myRet.toString());
		   System.out.println("This took : " + (myT2 - myT1));
		   Thread.sleep(1000);
		 }
	}

}
