package org.code.internet.pktest;

import org.jivesoftware.whack.ExternalComponentManager;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class UpperCaseComponent implements Component {
    private ExternalComponentManager mgr = null;

    public String getName() {
        return ("Upper case");
    }

    public String getDescription() {
        return ("Echos your message back to you in upper case");
    }

    public void processPacket(Packet packet) {
        if (packet instanceof Message) {
            org.xmpp.packet.Message original = (Message) packet;
            org.xmpp.packet.Message response = original.createCopy();
            //Swap the sender/recipient fields
            response.setTo(original.getFrom());
            response.setFrom(original.getTo());
            //Convert the text to upper case
        	
			System.out.println(original.getFrom() + " compoenet says: "
					+ original.getBody());
            response.setBody(original.getBody().toUpperCase());
            mgr.sendPacket(this, response);
        }
    }

    public void initialize(JID jid, ComponentManager componentManager) throws ComponentException {
        mgr = (ExternalComponentManager) componentManager;    
    }

    public void start() { }

    public void shutdown() { }
    
}