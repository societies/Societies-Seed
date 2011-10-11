package osgi.plain.exporter.impl;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


import osgi.plain.exporter.api.TimesTwoService;

public class ExporterActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
	
		System.out.println("Starting exporter bundle");
		SimpleTimesTwoService service =new SimpleTimesTwoService();
		context.registerService(TimesTwoService.class.getName(), service, null);		
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping importer bundle");
		
	}

}
