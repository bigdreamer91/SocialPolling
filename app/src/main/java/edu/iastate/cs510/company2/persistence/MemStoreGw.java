package edu.iastate.cs510.company2.persistence;

import java.util.ArrayList;
import java.util.List;

import edu.iastate.cs510.company2.gateway.Message;
import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.gateway.Record;


/**
 * 
 * This class actually acts more like an endpoint than a gateway. It unpacks the inbound 
 * message, delegates to an instance of memstore for the data, and 
 * composes appropriate response objects. 
 * @author Robertw
 *
 */
public class MemStoreGw implements PsGateway, GwMemStoreTestAccessor {

	protected MemStore store = new MemStore();
	
	@Override
	public Response send(Message msg) {
		List<Record> recordSet = new ArrayList<Record>();
		String topic = msg.getTopic();
		String key = msg.getParam("key");
		String index = msg.getParam("index");
		int pageSize = msg.getIntParam("pageSize");
		int id = msg.getIntParam("index");

		switch ( msg.getCommand().getOperation()){
			case read :
				try {
					int offset = 0;
					recordSet.clear();
					if (index == null){
						offset = store.read(topic, key, recordSet); //on read, return value is offset, indices is in recordSet

					}
					if (msg.getCallback()== null){
						return syncPloadResponse(msg.server, topic, key, recordSet, offset);
					}

				} catch (Exception e){
					//SHould really not be catching if we aren't even wrapping it and rethrowing..
					//not even logging? or sop?

				}
				break;
			case create:
				try {
					int rIndex = 0;
					recordSet.clear();
					String payload = msg.payload.get(key);
					if (index == null && (pageSize < 2 )){
						rIndex = store.create(topic, key, payload);
					}
					if (msg.getCallback()== null){
						return syncResponse(msg.server, topic, key, rIndex);
					}
				} catch (Exception e){

				}
				break;

			case addTopic:
				boolean state = store.addTopic(topic);
				return syncResponse(msg.getServer(), topic, (state)? 0 : -2);

			case update:
				recordSet.clear();//not really sure why we need to do this, are they trying to save memory or something silly?
				try {
					int rIndex = 0;
					String payload = msg.payload.get(key);
					if (id >= 0){
						rIndex = store.update(topic, key, id, payload);
					}
					if (msg.getCallback()== null){
						return syncResponse(msg.server, topic, key, rIndex);
					}
				} catch (Exception e){

				}
				break;
			case delete:
				try{
					store.delete(topic, key, id);
					return syncResponse(msg.server, topic, key, 1);
				} catch (Exception e){

				}
				break;
			case deleteTopic:

			default:

			}
			return syncResponse(msg.server, topic, key, 1);
	}

	private Response syncPloadResponse(String server, String topic, 
			String key, List<Record> recordSet, int offset) {
		BaseCbResponse rval = new BaseCbResponse(server, topic,
				recordSet, offset);
		rval.setMessage("New Topic created at "+topic);
		return rval;
	}

	private Response syncResponse(String server, String topic, int i) {
		BaseResponse rval = new BaseResponse(server, topic, "",  i);
		rval.setMessage("New Topic created at "+topic);
		return rval;
	}

	/**
	 * 
	 * constructs a simple, no payload response to be returned synchronously
	 * 
	 * @param server
	 * @param topic
	 * @param key
	 * @param rIndex
	 * @return
	 */
	private Response syncResponse(String server, String topic, String key, int rIndex) {
		BaseResponse rval =  new BaseResponse(server, topic, key, rIndex) ;
		
		return rval;
	}

	@Override
	public MemStore getMemStore() {
		return store;
	}

}
