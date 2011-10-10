package org.dummy.pkg;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class XMPPDependecyActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
	
		System.out.println("Starting exporter bundle");
			
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping importer bundle");
		
	}

}
