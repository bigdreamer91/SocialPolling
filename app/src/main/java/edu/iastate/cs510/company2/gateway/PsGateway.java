package edu.iastate.cs510.company2.gateway;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.iastate.cs510.company2.infrastructure.PluggableService;

/**
 * @author RobertW
 * 
 * This interface acts as a gateway to the functionality offered by an abstract persistence service. 
 * The Gateway hides details of the communication with the service ... even to the extent of hiding whether
 * the service is local or remote. 
 * 
 * The interface assumes the service stores "blobs" of opaque data, where "blobs" are indexed only by topic and a key. 
 * 
 * id | topic                                         | topic_key             | opaque_client_data
 * -------------------------------------------------------------------------------------------------------------------
 * 1 | registered_drivers_by_userName | Mich                     | username="Phil", phone="242-422-2242", location="campus town",vehicle="1960 Taurus"
 * 2 | registered_drivers_by_userName | GoldenBoy            | username="GoldenBoy", phone="242-422-6666", location="Ankeny",vehicle="2015 Lambourgini"
 * 3 | idle_drivers_by_location              | Demoines_Central | username="GoldenBoy", status="idle",last_update="Dec 12 2015", location="Demoines_Central", location_detail="Grand & MLK Parkway, Des Moines, IA"
 * 4 | transport_requests_by_location   | Ankeny                | username="Barbara", phone="242-423-7777", last_update="Sep 27 2016 11:00"
 * 
 * where id is a record index known only to the persistence service (i.e., a manufactured key)
 * where topics can be introduced at will by the client
 * where topic_key is the "key" in the map<String key,String value> specified in the gateway.request object and the gateway.response object, and
 * where opaque_client_data is the "value" in the same mapped pairs. You should treat that field as if it were encrypted, locked, black box.
 * 
 * In this example, relations (e.g., drivers to locations) are generally related by topics.
 * 
 * initial commands would be:
 * 
 * the command: read topic="registered_drivers_by_userName", key="Mich"
 * might be implemented at the service by performing the query: 
 * "select * from table x where topic='registered_drivers_by_userName" 
 * and topic_key="Mich" order ascending limit 25"
 * 
 * and return both the result and either -1 (no records remaining) or the index of the last returned record.
 * 
 * (Users of the service, however, should not assume anything about the implementation, as this kind 
 * of storag can just as easily be implemented as a set of files indexed by directory structure.) 
 * 
 *
 * subsequent read commands:
 * the command: read topic="registered_drivers_by_userName", key="Mich", page_start="index"
 * would fetch the next page (beginning with index) from the same query
 *
 * Be warned that future releases will probably define specific constants for implemented 
 * commands and command parameter names, thus, you should be careful to make whatever command and 
 * parameter names
 * you use initially very easy to change. 
 *
 */

public interface PsGateway extends PluggableService {
	
	public static enum Cmd {
		read,
		create,
		delete,
		update,
		addTopic,
		deleteTopic
	}
	
	public enum Status {
		success,
		failure,
		warning
	}
	
	public static class Command {
		
		private Cmd operation;
		private Map<String, String> namedParams = new HashMap<String,String>();
		
		public Command(Cmd op) {
			operation = op; 
		}

		public void setParam(String name, String value){
			namedParams.put(name, value);
		}
		
		public String getParam(String name){
			return namedParams.get(name);
		}

		public Cmd getOperation() {
			return operation;
		}
		
		public String getOpName(){
			return operation.name();
		}
	}
	
	public static interface Response {
		public PsGateway.Status getStatus(); // success/failure class of response
		public String getMajorCode(); //protocol level error
		public int getMinorCode(); //local or application level error
		public String getMessage(); // user appropriate description
		public Map<String, String> getDetails(); // technical details
		void addDetail(String key, String value);
	}
	public static interface CbResponse extends Response {
		public List<Record> getPayload();
	}
	
	public static interface Callback {
		public void deliver(PsGateway.CbResponse deferredResponse);
	}
	
	public PsGateway.Response send( Message msg );
}
