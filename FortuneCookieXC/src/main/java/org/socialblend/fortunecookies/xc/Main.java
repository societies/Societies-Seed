package org.socialblend.fortunecookies.xc;

import org.jivesoftware.whack.ExternalComponentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.ComponentException;

public class Main {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] arg) {
		new Main();
	}

	public Main() {
		org.xmpp.component.Component fortuneCookieComp = new FortuneCookieComponent();
		ExternalComponentManager manager = new ExternalComponentManager(
				"socialblend.local");
		manager.setSecretKey("fortunecookies.socialblend.local", "redman");

		log.info("Connected!");
		try {
			manager.addComponent("fortunecookies.socialblend.local",
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
