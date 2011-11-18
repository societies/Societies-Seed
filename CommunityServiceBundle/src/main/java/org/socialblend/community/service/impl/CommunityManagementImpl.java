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

package org.socialblend.community.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.socialblend.community.Community;
import org.socialblend.community.Participant;
import org.socialblend.community.Who;
import org.socialblend.community.service.CommunityManagement;
import org.socialblend.xmpp.xc.CommunicationException;
import org.socialblend.xmpp.xc.NamespaceExtension;
import org.socialblend.xmpp.xc.XCCommunication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;

@Component
public class CommunityManagementImpl implements CommunityManagement, NamespaceExtension {

	private XCCommunication xc;
	private Set<String> participants;
	private Set<String> leaders;
	
	@Autowired
	public CommunityManagementImpl(XCCommunication xc) {
		participants = new HashSet<String>();
		leaders = new HashSet<String>();
		this.xc = xc;
		try {
			xc.register(this); // TODO unregister??
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String getNamespace() {
		return "http://socialblend.org/community";
	}

	@Override
	public String getPackage() {
		return "org.socialblend.community";
	}

	@Override
	public void receiveMessage(Message message, Object pojo) {
		// do nothing
		// no use-case so far for community-received messages
	}

	@Override
	public Object receiveQuery(IQ iq, Object pojo) {
		// all received IQs contain a community element
		if (pojo.getClass().equals(Community.class)) {
			Community c = (Community) pojo;
			if (c.getJoin()!=null) {
				String jid = iq.getFrom().toBareJID();
				if (!participants.contains(jid)) {
					participants.add(jid);
				}
				// TODO add error cases to schema
				Community result = new Community();
				result.setJoin(""); // TODO check if jaxb behaves - no element should mean null and empty element should mean empty string
				return result;
			}
			if (c.getLeave()!=null) {
				String jid = iq.getFrom().toBareJID();
				if (participants.contains(jid)) {
					participants.remove(jid);
				}
				// TODO add error cases to schema
				Community result = new Community();
				result.setLeave(""); // TODO check if jaxb behaves - no element should mean null and empty element should mean empty string
				return result;
			}
			if (c.getWho()!=null) {
				// TODO add error cases to schema
				Community result = new Community();
				Who who = new Who();
				for (String jid : participants) {
					Participant p = new Participant();
					p.setJid(jid);
					if (leaders.contains(jid))
						p.setRole("leader");
					else
						p.setRole("participant");
					who.getParticipant().add(p);
				}
				result.setWho(who);
				return result;
			}
		}
		return null;
	}

	@Override
	public void receiveResult(IQ iq, Object pojo) {
		// do nothing
		// no use-case so far for community-sent iqs, so it doesn't need to handle results
	}

	@Override
	public void receiveError(IQ iq) {
		// do nothing
		// no use-case so far for community-sent iqs, so it doesn't need to handle errors
	}
	
	@Override
	public Set<String> getParticipants() {
		return new HashSet<String>(participants);
	}

	@Override
	public Set<String> getLeaders() {
		return new HashSet<String>(leaders);
	}

	@Override
	public String getOwner() {
		// TODO Auto-generated method stub
		return null;
	}
}
