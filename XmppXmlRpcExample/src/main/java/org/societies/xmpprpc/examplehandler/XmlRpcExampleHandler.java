package org.societies.xmpprpc.examplehandler;

public class XmlRpcExampleHandler implements IXmlRpcExampleHandler {

	public String getStateName(Integer aStateId)
	{
		System.out.println("get state being called. ");
		return "Switzerland";
	}
	
	public String getRandomQuote() {
		return "To be or not to be.";
	}
	
}
