package org.societies.xmpp.rpc.testing;

import org.societies.xmpprpc.examplehandler.IClientRpcHandler;

public class LocalDelegator implements IClientRpcHandler {
	  //org.societies.xmpp.rpc.testing.LocalDelegator.setData
	@Override
    public int setChartContainer() {
    	 System.out.println("setChartContainer");
        return 0; 
    }
    
   @Override
    public int setData(Integer num) {
    	 System.out.println("setData");
    	 try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       return 0;  
   }

}