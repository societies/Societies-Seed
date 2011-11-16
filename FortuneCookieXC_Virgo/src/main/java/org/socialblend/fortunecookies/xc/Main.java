package org.socialblend.fortunecookies.xc;

import org.jivesoftware.whack.ExternalComponentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.ComponentException;

public class Main {

	private Logger log = LoggerFactory.getLogger(this.getClass());

//	public static void main(String[] arg) {
//		new Main();
//	}
	
	public Main() {
	
		System.out.println(" *** fortunecookies.xc bean started ***");
		
		org.xmpp.component.Component fortuneCookieComp = new FortuneCookieComponent();
//		ExternalComponentManager manager = new ExternalComponentManager(
//				"red.local");
//		manager.setSecretKey("fortunecookies.red.local", "password.red.local");
		ExternalComponentManager manager = new ExternalComponentManager(
		"jmgoncalv.es");
		manager.setSecretKey("xc.jmgoncalv.es", "nightmare");
		
		log.info("Connected!");	
		try {
			manager.addComponent("xc.jmgoncalv.es",
					fortuneCookieComp);
		} catch (ComponentException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
