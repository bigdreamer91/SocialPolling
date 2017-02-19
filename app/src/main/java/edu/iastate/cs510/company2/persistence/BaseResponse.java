package edu.iastate.cs510.company2.persistence;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import edu.iastate.cs510.company2.gateway.PsGateway.Response;
import edu.iastate.cs510.company2.gateway.PsGateway.Status;

public class BaseResponse implements Response{
	private String majorCode = ""+HttpURLConnection.HTTP_OK;
	private Status status = Status.success;
	private int minorCode = 0; 
	private String message = "Message Accepted";
	private Map<String, String> details =  new HashMap<String,String>();
	
	
	public BaseResponse(){
		
	}
	
	public BaseResponse(String server, String topic, String key, int rIndex){
		//TODO: add server to response? 
		if (rIndex >= 0){
			addDetail("topic", topic);
			addDetail("key", key);
			addDetail("index", ""+rIndex);
			message = "New record created at "+topic+":"+key;
		} else {
			majorCode = ""+HttpURLConnection.HTTP_INTERNAL_ERROR;
			minorCode = rIndex;
			status = Status.failure;
		}
		
	}
	
	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public String getMajorCode() {
		return majorCode;
	}

	@Override
	public int getMinorCode() {
		return minorCode;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public Map<String, String> getDetails() {
		return details ;
	}
	
	@Override
	public void addDetail(String key, String value){
		details.put(key, value);
	}

	public void setMessage(String msg) {
		message = msg;
	}

}
