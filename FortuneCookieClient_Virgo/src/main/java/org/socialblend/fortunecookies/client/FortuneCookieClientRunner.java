package org.socialblend.fortunecookies.client;

import java.io.IOException;

public class FortuneCookieClientRunner {

	public FortuneCookieClientRunner() {
		
		System.out.println(" *** Starting fortune Cookie Client bean ***");
		
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
	
}
