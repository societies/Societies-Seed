package org.socialblend.fortunecookies;

import org.xmpp.component.AbstractComponent;
import org.xmpp.packet.Message;

public class FortuneCookieXC extends AbstractComponent {

	@Override
	public String getDescription() {
		return "A component thta dispatches wisdom in the form of XMPP Fortune cookies";
	}

	@Override
	public String getName() {
		return "Fortune Cookie Component";
	}

	@Override
	protected void handleMessage(Message received) {
		// construct the response
		Message response = new Message();
		response.setFrom(jid);
		response.setTo(received.getFrom());
		response.setBody("Hello!");

		// send the response using AbstractComponent#send(Packet)
		send(response);
	}

}
