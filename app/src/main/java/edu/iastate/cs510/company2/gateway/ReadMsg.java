package edu.iastate.cs510.company2.gateway;

import edu.iastate.cs510.company2.gateway.PsGateway.Callback;
import edu.iastate.cs510.company2.gateway.PsGateway.Cmd;

public class ReadMsg extends Message {

	public ReadMsg(String addr, String topic, String key){
		super.server = addr; 
		super.topic = topic;
		super.cmd = new PsGateway.Command(Cmd.read);
		cmd.setParam("key", key);
		super.callback = null; //default. Gateway will supply callback				
	}
	
	public void setParam(String key, String value){
		cmd.setParam(key, value);
	}
	
	public PsGateway.Response send(PsGateway gw, Callback callback){
		super.callback = callback;
		return gw.send(this);
	}
	
	public PsGateway.CbResponse send(PsGateway gw) {
		 return (PsGateway.CbResponse) gw.send(this);
	}
} 
