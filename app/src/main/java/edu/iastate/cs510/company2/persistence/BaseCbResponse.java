package edu.iastate.cs510.company2.persistence;

import java.util.ArrayList;
import java.util.List;

import edu.iastate.cs510.company2.gateway.PsGateway.CbResponse;
import edu.iastate.cs510.company2.gateway.Record;

public class BaseCbResponse extends BaseResponse implements CbResponse {

	List<Record> content = new ArrayList<Record>();
	
	public BaseCbResponse (String server, String topic, List<Record> recordSet, int offset){
		super(server, topic, "", 0);
		for (Record item: recordSet){
			content.add(new Record( topic, item.key,  item.index, item.pLoad));
		}
		super.addDetail("nextOffset", ""+offset);
	}
	
	public BaseCbResponse() {
	}

	@Override
	public List<Record> getPayload() {
		return content;
	}
	
	public void addRecord(String key, String data){
		content.add(new Record("", key, 0, data));
	}
	
}
