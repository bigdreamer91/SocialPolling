package edu.iastate.cs510.company2.persistence;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import edu.iastate.cs510.company2.gateway.AddTopicMsg;
import edu.iastate.cs510.company2.gateway.CreateMsg;
import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.gateway.PsGateway.CbResponse;
import edu.iastate.cs510.company2.gateway.PsGateway.Status;
import edu.iastate.cs510.company2.gateway.ReadMsg;
import edu.iastate.cs510.company2.testsupport.MockData;

import static org.junit.Assert.assertEquals;

public class TestMemStoreGw {
	MemStoreGw gw = null;
	MemStore store = null;
	
	@Before
	public void setup() {
		gw = new MemStoreGw();
		store = ((GwMemStoreTestAccessor) gw).getMemStore();
	}
	
	@Test
	public void test() {
		store.clear();
		AddTopicMsg topic = new AddTopicMsg( "http://iastate.510.com/SocialPolling", "Poll" );
		PsGateway.Response reply = topic.send(gw);
		assertEquals(Status.success, reply.getStatus())	;
	
		CreateMsg newPollMessage = new CreateMsg("http://iastate.510.com/SocialPolling", "General", "Poll", MockData.getPoll());
		reply = newPollMessage.send(gw);
		assertEquals(Status.success, reply.getStatus());
		assertEquals("General", reply.getDetails().get("key"));
		assertEquals("100", reply.getDetails().get("index"));
		
		ReadMsg read = new ReadMsg("http://iastate.510.com/SocialPolling", "Poll", "General");
		reply = read.send(gw);
		CbResponse fullReply = null;
		if (reply instanceof CbResponse){
			fullReply = (CbResponse) reply;
		}
		assertEquals(Status.success, reply.getStatus());
		assertEquals(1, fullReply.getPayload().size());
		assertEquals("General", fullReply.getPayload().get(0).key);
		//TODO: Move marshalling to helper method and use that method for all comparisons
		assertEquals(new Gson().toJson(MockData.getPoll()), fullReply.getPayload().get(0).pLoad);

	}
		//TODO:
		// test read by index
		// test multi-page response
		// check details of error responses at gateway level
		// implement a "remaining" return param (detail) to 
		// indicate how many records follow nextOffset in read query. 
		// test for page-size behavior
		


}
