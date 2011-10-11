package org.spring.osgi.exporter.internal;

import org.spring.osgi.exporter.api.IHelloworld;

public class HelloWorldImpl implements IHelloworld {

	public HelloWorldImpl()	{
		
		System.out.println(" ******** Hi, HelloWorldImpl bean in started ******* ");
		
	}
	
	public String echoHellowWorld() {
		
		return "***** Hello World received from Exporter *****";
		
	}

}
