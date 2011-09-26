package org.socialblend.fortunecookies;

import org.jivesoftware.whack.ExternalComponent;
import org.jivesoftware.whack.ExternalComponentManager;
import org.xmpp.component.ComponentException;


public class Main  {

	public static void main(String[] arg){
		org.xmpp.component.Component fortuneCookieComp = new FortuneCookieXC();
		ExternalComponentManager externalComponentMngr = new ExternalComponentManager("localhost");
		ExternalComponent fortuneCookieXC = new ExternalComponent(fortuneCookieComp, externalComponentMngr);
		
		try {
			fortuneCookieXC.connect("socialblend.local", 5275, "fortunecookies.socialblend.local");
		} catch (ComponentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
