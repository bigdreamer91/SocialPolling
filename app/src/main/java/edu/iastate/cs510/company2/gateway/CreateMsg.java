package edu.iastate.cs510.company2.gateway;

import com.google.gson.Gson;

import edu.iastate.cs510.company2.models.Poll;

import static edu.iastate.cs510.company2.gateway.PsGateway.Cmd.create;

public class CreateMsg extends Message {

	public CreateMsg(String addr,String topic, String key, String blob){
		super();
		super.server = addr; 
		super.topic = topic;
		super.cmd = new PsGateway.Command(create);
		cmd.setParam("pageSize", "1");
		cmd.setParam("key", key);
		super.payload.put(key, blob);
		super.callback = null; //create never needs deferred? 
	}

	public CreateMsg(String addr, String topic, String key, Poll poll){
		this(addr, topic, key, new Gson().toJson(poll));
	}
	
	public PsGateway.Response send(PsGateway gw){
		return gw.send(this);
	}

}
