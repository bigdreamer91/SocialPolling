package edu.iastate.cs510.company2.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.iastate.cs510.company2.gateway.PsStatus;
import edu.iastate.cs510.company2.gateway.Record;

public class MemStore {

	public static class Blob {
		public static final int kSerialDefault = 100;

		protected static int nextSerial = kSerialDefault;
		private String data;
		private int serial;

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Blob blob = (Blob) o;

			return data != null ? data.equals(blob.data) : blob.data == null;

		}

		@Override
		public int hashCode() {
			return data != null ? data.hashCode() : 0;
		}

		public Blob (String data){
			this.data = data;
			this.serial = nextSerial++;
		}

		public Blob (String data, int index){
			this.data = data;
			this.serial = index;
		}
		
		public String getData(){
			return this.data;
		}
		
		public int getSerial() {
			return this.serial;
		}
	}
	 
	/**
	 * A multimap with two level (topic->key) index. Entries are
	 * assigned a unique integer index as they are created. 
	 */
	private Map<String,Map<String,List<Blob>>> topics
		= new HashMap<String, Map<String, List<Blob>>>();


	/**
	 * @param topic
	 * @param key
	 * @param payload
	 * @return -1 if bin topic does not exist. 
	 *         -2 if key/record already exist. 
	 *         Otherwise record index. 
	 */
	public int create(String topic, String key, String payload){
		Map<String, List<Blob>> bin = topics.get(topic);
		List<Blob> cluster = null; 
		if (bin == null)
			addTopic(topic);
			bin = topics.get(topic);

		//Frankly why can't we store the same record twice at a given topic/key? Depending on what we decide to store it is very
		//possible that we can store two of the same value at the same location.. e.g.
		//User 1 creates poll Q: 'Yes or No?' with Choices 'Yes' and 'No'
		//User 2 creates the same poll. We happen to be storing the user that created it, timestamp etc but it's perfectly reasonable
		//that two rows will look the same under same topic/key
		if ( (cluster = bin.get(key)) != null && isDuplicate(cluster, payload)){
			return PsStatus.DUPE.getValue();			
		}
		else {
			// no entry for this key, create a key cluster
			if (cluster == null){
				cluster = new ArrayList();
				bin.put(key, cluster);
			}
			// now put payload into key cluster
			Blob entry = new Blob(payload, cluster.size());
			cluster.add(entry);
			return entry.getSerial();
		}
	}

	public int read(String topic, String key, List<Record> result ) {
		List<Blob> cluster= null; 
		Map<String,List<Blob>> bin = topics.get(topic);
		if (bin == null) {

			return PsStatus.NOTOPIC.getValue();
		}
		else if ( (cluster = bin.get(key)) == null) {
			return PsStatus.NOKEY.getValue();			
		} else {
			int offset = 0;
			for (Blob item: cluster){
				result.add(new Record(topic, key, item.getSerial(), item.getData()));
				offset ++;
			}
			return offset;
		}
	}

	public int update(String topic, String key, int id, String payload) {
		Map<String, List<Blob>> bin = topics.get(topic);
		List<Blob> cluster = null;

		//Create entry with the payload
		Blob entry = new Blob(payload);

		//If we are updating we expect topic to already exist
		if (bin == null){
			return PsStatus.NOTOPIC.getValue();
		}

		//If we are updating we expect key to already exist
		cluster = bin.get(key);
		if(cluster == null){
			return PsStatus.NOKEY.getValue();
		}

		//If we are updating we expect at least one record to exist
		if(cluster.isEmpty()) {
			return PsStatus.NOBLOB.getValue();
		}

		if(cluster.get(id) != null){
			cluster.set(id, entry);
		}

		return id;
	}

	public int delete(String topic, String key, int id){
		Map<String, List<Blob>> bin = topics.get(topic);
		List<Blob> cluster = null;

		//If we are deleting we expect topic to already exist
		if (bin == null){
			return PsStatus.NOTOPIC.getValue();
		}

		//If we are deleting we expect key to already exist
		cluster = bin.get(key);
		if(cluster == null){
			return PsStatus.NOKEY.getValue();
		}

		//If we are deleting we expect at least one record to exist
		if(cluster.isEmpty()) {
			return PsStatus.NOBLOB.getValue();
		}

		if(cluster.get(id) != null){
			cluster.remove(id);
		}
		return PsStatus.OK.getValue();
	}

	public List<String> addTopic(String[] topics){
		List<String> rval = new ArrayList<String>();
		for (int i = 0; i < topics.length; i++){
			if ( ! addTopic(topics[i])){
				rval.add(topics[i]);
			}
		}
		return rval;
	}
	
	public boolean addTopic(String topic) {
		if ( topics.keySet().contains(topic)) {			
			return false; 
		} else {
			topics.put(topic, new HashMap<String,List<Blob>>());
			return true;
		}
	}

	public void clear() {
		topics = new HashMap<String, Map<String, List<Blob>>>();
		Blob.nextSerial = Blob.kSerialDefault;
	}

	private boolean isDuplicate(Collection<Blob> cluster, String payload) {
		//Implement .toEquals and we can simply use contains instead of iterating ourselves, this will save cpu cycles for
		//hashsets and maps etc
		if(cluster.contains(payload)){
			return true;
		}
		return false;
	}
}
