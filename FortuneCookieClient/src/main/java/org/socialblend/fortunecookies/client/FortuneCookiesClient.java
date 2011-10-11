package org.socialblend.fortunecookies.client;

import java.io.IOException;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FortuneCookiesClient {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Connection connection;

	private final String username;
	private final String password;
	private String resource = "NoResource";

	public static void main(String[] args) {
		FortuneCookiesClient fcc = new FortuneCookiesClient("red",
				"red.local", "red", "home");

		fcc.start();
		try {
			System.out.println("Press enter to stop");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Shutdown on user input at the console
		fcc.stop();
		System.exit(0);
	}

	public FortuneCookiesClient(String login, String server, String password,
			String resource) {
		this.username = login;
		this.password = password;
		this.resource = resource;
		this.connection = new XMPPConnection(server);
		log.debug("Fortune Cookie reader created at " + username + "@" + server
				+ "/" + resource);
	}

	public void start() {
		try {
			connection.connect();
			connection.login(username, password, resource);

			PacketListener listener = new PacketListener() {

				public void processPacket(Packet packet) {
					
					if (packet instanceof Message) {
						Message msg = (Message) packet;
						System.out.println(msg.getFrom() + " says: "
								+ msg.getBody());
					} else {
						System.out.println(packet.getFrom()
								+ " received and I'm... I'm just confused. Is the FortuneCookieXC running?");
					}
				}
			};

			connection.addPacketListener(listener, null);

			// Send a message
			Message msg = new Message(
					"fortune@fortunecookies.red.local",
					org.jivesoftware.smack.packet.Message.Type.normal);
			
			msg.setBody("Good Morrow, Dear Sir!");
			connection.sendPacket(msg);

		} catch (XMPPException e) {
			throw new RuntimeException("XMPP Error", e);
		}
	}

	public void stop() {
		log.debug("Disconnecting");
		connection.disconnect();
	}
}
