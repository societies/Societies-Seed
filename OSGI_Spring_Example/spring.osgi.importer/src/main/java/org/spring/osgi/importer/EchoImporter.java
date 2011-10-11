package org.spring.osgi.importer;

import org.spring.osgi.exporter.api.IHelloworld;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class EchoImporter {

	private IHelloworld helloWorld;
	
	@Autowired
	public EchoImporter(IHelloworld helloworld){
		this.helloWorld=helloworld;
		System.out.println(helloWorld.echoHellowWorld());
	}
	
//	@PostConstruct
//	public void init(){
//		System.out.println(helloWorld.echoHellowWorld());
//	}
}
