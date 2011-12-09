package org.societies.xmpprpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.TypeConverter;
import org.apache.xmlrpc.common.TypeConverterFactoryImpl;
import org.apache.xmlrpc.common.XmlRpcInvocationException;
import org.apache.xmlrpc.common.XmlRpcNotAuthorizedException;
import org.apache.xmlrpc.metadata.Util;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcNoSuchHandlerException;
import org.apache.xmlrpc.server.AbstractReflectiveHandlerMapping.AuthenticationHandler;

import sun.security.krb5.internal.MethodData;

public class InstanceBasedHandler extends PropertyHandlerMapping {
	
	HashMap<String, Object> theInstanceHandlers = new HashMap<String, Object>();

	public void addHandler(String pKey, Object anObject) throws XmlRpcException {
        registerPublicMethods(pKey, anObject.getClass());
        theInstanceHandlers.put(pKey, anObject);
    }

	  /** Returns the {@link XmlRpcHandler} with the given name.
     * @param pHandlerName The handlers name
     * @throws XmlRpcNoSuchHandlerException A handler with the given
     * name is unknown.
     */
    public XmlRpcHandler getHandler(String pHandlerName)
            throws XmlRpcNoSuchHandlerException, XmlRpcException {
    	if(!theInstanceHandlers.containsKey(pHandlerName))
    	{
    		if(pHandlerName.indexOf(".")!=-1)
    			pHandlerName = pHandlerName.substring(0, pHandlerName.indexOf("."));
    		return new InstanceXmlRpcHandler(theInstanceHandlers.get(pHandlerName));
    	}
    	
        XmlRpcHandler result = (XmlRpcHandler) handlerMap.get(pHandlerName);
        if (result == null) {
            throw new XmlRpcNoSuchHandlerException("No such handler: " + pHandlerName);
        }
        return result;
    }
	
}

class InstanceXmlRpcHandler  implements XmlRpcHandler {
	
	Object theObject; 
	public InstanceXmlRpcHandler(Object anObject){
		theObject = anObject; 		
	}
	
	@Override
	public Object execute(XmlRpcRequest pRequest) throws XmlRpcException {	
		
	    Object[] args = new Object[pRequest.getParameterCount()];
	    Class[] argClasses = new Class[pRequest.getParameterCount()];
	    for (int j = 0;  j < args.length;  j++) {
	        args[j] = pRequest.getParameter(j);
	        argClasses[j] = pRequest.getParameter(j).getClass();	        
	    }
	    try{
	    	
//	    	Method[] myMethods = theObject.getClass().getMethods();
//	    	//
//	        // Get all declared methods including public, protected, private and
//	        // package (default) access but excluding the inherited methods.
//	        //
//	        Method[] methods = theObject.getClass().getDeclaredMethods();
//	        for (Method method : methods) {
//	            System.out.println("Method name        = " + method.getName());
//	            System.out.println("Method return type = " + method.getReturnType().getName());
//
//	            Class[] paramTypes = method.getParameterTypes();
//	            for (Class c : paramTypes) {
//	                System.out.println("Param type         = " + c.getName());
//	                System.out.println("Param type class name         = " + c.getClass().getName());
//	            }
//
//	            System.out.println("---------"+ int.class.getName() + "-------------");
//	        }
	        
	        String mNameWithHKey=pRequest.getMethodName();
	    	String mName = mNameWithHKey.substring(mNameWithHKey.indexOf(".")+1);
	        Method myMethod = theObject.getClass().getMethod(mName, argClasses);
		    Object instance = theObject;
	        for (int j = 0;  j < args.length;  j++) {
	            args[j] = new TypeConverterFactoryImpl().getTypeConverter(args[j].getClass()).convert(args[j]);
	        }
	        return invoke(instance, myMethod, args);
	    }
	    catch(NoSuchMethodException anEx){
	    	throw new XmlRpcException("No method matching arguments: " + Util.getSignature(args));
	    }
    }

    private Object invoke(Object pInstance, Method pMethod, Object[] pArgs) throws XmlRpcException {
        try {
	        return pMethod.invoke(pInstance, pArgs);
	    } catch (IllegalAccessException e) {
	        throw new XmlRpcException("Illegal access to method "
	                                  + pMethod.getName() + " in class "
	                                  + theObject.getClass().getName(), e);
	    } catch (IllegalArgumentException e) {
	        throw new XmlRpcException("Illegal argument for method "
	                                  + pMethod.getName() + " in class "
	                                  + theObject.getClass().getName(), e);
	    } catch (InvocationTargetException e) {
	        Throwable t = e.getTargetException();
            if (t instanceof XmlRpcException) {
                throw (XmlRpcException) t;
            }
	        throw new XmlRpcInvocationException("Failed to invoke method "
	                                  + pMethod.getName() + " in class "
	                                  + theObject.getClass().getName() + ": "
	                                  + t.getMessage(), t);
	    }
	}

	
}
