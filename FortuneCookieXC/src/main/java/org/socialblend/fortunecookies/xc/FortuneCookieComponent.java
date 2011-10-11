package org.socialblend.fortunecookies.xc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.AbstractComponent;
// import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

public class FortuneCookieComponent extends AbstractComponent {
	@SuppressWarnings("unused")
	private Logger log = LoggerFactory.getLogger(this.getClass());
//	private JID myAddress = null;

	public String getDescription() {
		return "A component thta dispatches wisdom in the form of XMPP Fortune cookies";
	}

	public String getName() {
		return "Fortune Cookie Component";
	}
	
//	public void final initialize(JID jid, ComponentManager componentManager)
//			throws ComponentException {
//		this.myAddress = jid;
//	}

	protected void handleMessage(Message received) {
		String cookie = Wisdom.getCookie();
		// construct the response
		Message response = new Message();
		response.setFrom(jid);
		response.setTo(received.getFrom());
		response.setBody(cookie);
		// send the response using AbstractComponent#send(Packet)
		send(response);
		// Report
		System.out.println(received.getFrom() + " said: " + received.getBody()
				+ ". I personally think that " + cookie);

	}

}
