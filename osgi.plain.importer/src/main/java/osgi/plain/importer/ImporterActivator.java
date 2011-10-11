package osgi.plain.importer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import osgi.plain.exporter.api.TimesTwoService;


public class ImporterActivator implements BundleActivator {
	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping importer bundle");
		
	}

	public void start(BundleContext context) throws Exception {
		System.out.println("Starting importer bundle");	
		ServiceReference reference = context.getServiceReference(TimesTwoService.class.getName());
		TimesTwoService service = (TimesTwoService) context.getService(reference);
		System.out.println(" *** Result of 3 X 2 =" + service.multiply(3)+" ***");
		context.ungetService(reference);		
	}
	
}
