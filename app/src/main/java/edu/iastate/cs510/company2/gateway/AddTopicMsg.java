package edu.iastate.cs510.company2.gateway;

import edu.iastate.cs510.company2.gateway.PsGateway.Cmd;
import edu.iastate.cs510.company2.gateway.PsGateway.Response;

public class AddTopicMsg extends Message {
	
	public AddTopicMsg(String addr, String topic ){
		super();
		super.server = addr;
		super.topic = topic;
		super.cmd = new PsGateway.Command(Cmd.addTopic);
		super.callback = null; //create never needs deferred?
	}
	
	public Response send(PsGateway gw){
		return gw.send(this);
	};
}

