package osgi.plain.exporter.impl;

import osgi.plain.exporter.api.TimesTwoService;

public class SimpleTimesTwoService implements TimesTwoService {

	public int multiply(int input) {
		return 2*input;
	}

}
