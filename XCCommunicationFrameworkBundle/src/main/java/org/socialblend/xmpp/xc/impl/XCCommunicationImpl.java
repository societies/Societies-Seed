/**
 * Copyright (c) 2011, SOCIETIES Consortium
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * @author jmgoncalves
 */

package org.socialblend.xmpp.xc.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.io.SAXReader;
import org.jivesoftware.whack.ExternalComponentManager;
import org.socialblend.xmpp.xc.CommunicationException;
import org.socialblend.xmpp.xc.NamespaceExtension;
import org.socialblend.xmpp.xc.XCCommunication;
import org.springframework.stereotype.Controller;
import org.xml.sax.InputSource;
import org.xmpp.component.AbstractComponent;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.Message;
import org.xmpp.packet.PacketError;

// TODO problem list
// it is single threaded and can be stuck by the synchronous calls to the extensions - should support "send and forget" to the registered extensions
// only supports one extension per namespace - should split between "namespaceExtension" and "bundleExtension"
// dom4j parsing just sucks to use with jaxb - should cut dom4j out of it and just store the Packet as a char array
// only supports one pojo per stanza
@Controller
public class XCCommunicationImpl extends AbstractComponent implements XCCommunication {

	private static final String JABBER_CLIENT = "jabber:client";
	private static final String JABBER_SERVER = "jabber:server";
	
	private String host;
	private String subDomain;
	private String secretKey;
	private ExternalComponentManager manager;
	//private Map<String,Collection<NamespaceExtension>> extensions;
	private Map<String,NamespaceExtension> extensions;
	private Map<String,NamespaceExtension> queryIdExtension;
	private Map<String,Unmarshaller> unmarshallers;
	private Map<Class<?>,Marshaller> marshallers;
	
	private SAXReader reader;
	
	public XCCommunicationImpl(String host, String subDomain, String secretKey) {
		this.host = host;
		this.subDomain = subDomain;
		this.secretKey = secretKey;
		
		//extensions = new HashMap<String, Collection<NamespaceExtension>>();
		extensions = new HashMap<String, NamespaceExtension>();
		queryIdExtension = new HashMap<String, NamespaceExtension>();
		unmarshallers = new HashMap<String, Unmarshaller>();
		marshallers = new HashMap<Class<?>, Marshaller>();
		
		reader = new SAXReader();
		
		manager = new ExternalComponentManager(host);
		manager.setSecretKey(subDomain, secretKey);
		
		log.info("Connected!");
		try {
			manager.addComponent(subDomain,this);
		} catch (ComponentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDescription() {
		return "This component is just amazing";
	}

	@Override
	public String getName() {
		return "SocialBlend Extensible External Component";
	}
	
	@Override
	public void register(NamespaceExtension nsExt) throws CommunicationException, ClassNotFoundException {
//		Collection<NamespaceExtension> currentExtensions = extensions.get(nsExt.getNamespace());
//		if (currentExtensions==null) {
//			currentExtensions = new ArrayList<NamespaceExtension>();
//			extensions.put(nsExt.getNamespace(), currentExtensions);
//		}
//		currentExtensions.add(nsExt);
		// latest sticks!
		log.info("register "+nsExt.getNamespace());
		try {
			extensions.put(nsExt.getNamespace(),nsExt);
			JAXBContext jc = JAXBContext.newInstance(nsExt.getPackage());
			unmarshallers.put(nsExt.getNamespace(), jc.createUnmarshaller());
			Marshaller mr = jc.createMarshaller();
			
			// is not loading join and leave because they are not complextypes
			Class<?> objFactory = Class.forName(nsExt.getPackage()+".ObjectFactory");
			for (Method m : objFactory.getMethods()) {
				if (m.getName().startsWith("create")) {
					XmlRootElement re = m.getReturnType().getAnnotation(XmlRootElement.class);
					if (re!=null) {
						marshallers.put(m.getReturnType(),mr);
					}
				}
			}
		} catch (JAXBException e) {
			throw new CommunicationException("Could not register NamespaceExtension... caused by JAXBException: ", e);
		}
	}

	@Override
	protected String[] discoInfoFeatureNamespaces() {
		String[] returnArray = new String[extensions.size()];
		return extensions.keySet().toArray(returnArray);
	}

	@Override
	protected IQ handleIQGet(IQ iq) throws Exception {
		log.info("handleIQGet");
		Element any = (Element) iq.getElement().elements().get(0); // according to the schema in RCF3921 IQs only have one element, unless they have an error
		NamespaceExtension nsExtension = extensions.get(any.getNamespace().getURI());
		Unmarshaller u = unmarshallers.get(any.getNamespace().getURI());
		
		log.info("extensions.size()="+extensions.size()+"; unmarshallers.size()="+unmarshallers.size());
		log.info("extensionsKey:"+extensions.keySet().iterator().next()+"; unmarshallersKey:"+unmarshallers.keySet().iterator().next());
		log.info("extensionsContains:"+extensions.containsKey(any.getNamespace().getURI())+"; unmarshallersContains:"+unmarshallers.containsKey(any.getNamespace().getURI()));
		log.info("handleIQGet xmlns="+any.getNamespace().getURI()+" ext="+nsExtension+" u="+u);
		IQ returnIq = null;
		if (extensions!=null && u!=null) {
			// TODO DISCLAIMER: this jaxb-dom4j conversion code is VERY BAD but i have to rush this; the propper solution would be to rewrite whack
			Object pojo = u.unmarshal(new InputSource(new StringReader(any.asXML())));
			Object result = nsExtension.receiveQuery(iq, pojo);
			returnIq = new IQ(Type.result, iq.getID());
			returnIq.setTo(iq.getFrom());
			ByteArrayOutputStream os = new ByteArrayOutputStream(); 
			marshallers.get(result.getClass()).marshal(result, os);
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
	        Document document = reader.read(is);
			returnIq.getElement().add(document.getRootElement());
		}
		else {
			returnIq = new IQ(Type.error, iq.getID());
			returnIq.setTo(iq.getFrom());
			PacketError error = new PacketError(PacketError.Condition.service_unavailable, PacketError.Type.cancel);
			returnIq.getElement().add(error.getElement());
		}
		return returnIq;
	}

	@Override
	protected IQ handleIQSet(IQ iq) throws Exception {
		return handleIQGet(iq);
	}
	
	@Override
	public void sendQuery(IQ iq, Object pojo, NamespaceExtension callback) throws Exception {
		// TODO DISCLAIMER: this jaxb-dom4j conversion code is VERY BAD but i have to rush this; the propper solution would be to rewrite whack
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		marshallers.get(pojo.getClass()).marshal(pojo, os);
		ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        Document document = reader.read(is);
		iq.getElement().add(document.getRootElement());
		queryIdExtension.put(iq.getID(), callback);
	}
	
	@Override
	protected void handleIQResult(IQ iq) {
		Element any = (Element) iq.getElement().elements().get(0); // according to the schema in RCF3921 IQs only have one element, unless they have an error
		NamespaceExtension callback = queryIdExtension.get(iq.getID());
		Unmarshaller u = unmarshallers.get(any.getNamespace());
		try {
			if (callback!=null && u!=null) {
				// TODO DISCLAIMER: this jaxb-dom4j conversion code is VERY BAD but i have to rush this; the propper solution would be to rewrite whack
				Object pojo = u.unmarshal(new InputSource(new StringReader(any.asXML())));
				callback.receiveResult(iq, pojo);
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void handleIQError(IQ iq) {
		NamespaceExtension callback = queryIdExtension.get(iq.getID());
		if (callback!=null) {
			callback.receiveError(iq);
		}
	}

	@Override
	public void sendMessage(Message message, Object pojo) throws Exception {
		if (pojo!=null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			marshallers.get(pojo.getClass()).marshal(pojo, os);
			ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
	        Document document = reader.read(is);
			message.getElement().add(document.getRootElement());
		}
	}
	
	@Override
	protected void handleMessage(Message message) {
		// according to the schema in RCF3921 messages have an unbounded number of "subject", "body" or "thread" elements before the any element part
		Element any = null;
		for (Object o : message.getElement().elements()) {
			Namespace ns = ((Element)o).getNamespace();
			if (!(ns.equals(JABBER_CLIENT) || ns.equals(JABBER_SERVER))) {
				any = (Element) o;
				break;
			}
		}
		NamespaceExtension nsExtension = extensions.get(any.getNamespace().toString());
		Unmarshaller u = unmarshallers.get(any.getNamespace());
		try {
			if (extensions!=null && u!=null) {
				// TODO DISCLAIMER: this jaxb-dom4j conversion code is VERY BAD but i have to rush this; the propper solution would be to rewrite whack
				Object pojo = u.unmarshal(new InputSource(new StringReader(any.asXML())));
				nsExtension.receiveMessage(message, pojo);
			}
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
