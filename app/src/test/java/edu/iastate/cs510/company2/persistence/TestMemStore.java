package edu.iastate.cs510.company2.persistence;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import edu.iastate.cs510.company2.gateway.PsStatus;
import edu.iastate.cs510.company2.gateway.Record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestMemStore {

	MemStore store = new MemStore();
	
	@Test
	public void testTopics() {
		store.clear();
		
		List<Record> result = new ArrayList<Record>();
		int status = store.read("My Topic", "Blues", result );
		assertEquals( PsStatus.NOTOPIC.getValue(), status);
		assertEquals( 0, result.size());

		boolean success = store.addTopic("My Topic");
		assertTrue(success);
		
		status = store.read("My Topic", "Blues", result );
		assertEquals( PsStatus.NOKEY.getValue(), status);
		assertEquals( 0, result.size());
		
		// write a record
		status = store.create("My Topic", "Blues", "song = my favorite");
		System.out.println("create returns "+status);
		assertTrue(status >= PsStatus.OK.getValue());
	    assertTrue(status == 0);

		// retrieve that record
		status = store.read("My Topic", "Blues", result );
		assertEquals( 1, status);
		assertEquals( 1, result.size());
		assertEquals(0, result.get(0).index);
		assertEquals(result.get(0).pLoad,"song = my favorite");

		// write a record
		status = store.create("My Topic", "Blues", "song = different stuff");
		System.out.println("create returns "+status);
		assertTrue(status >= PsStatus.OK.getValue());
	    assertTrue(status == 1);

		// retrieve the set
		result.clear();
	    status = store.read("My Topic", "Blues", result );
		assertEquals( 2, status);
		assertEquals( 2, result.size());
		assertListContains("song = different stuff",result);
		assertListContains("song = my favorite",result);	
	}

	private boolean assertListContains(String expected, List<Record> result) {
		for (Record item : result){
			if (item.pLoad.contentEquals(expected)){
				return true;
			}
		}
		fail("Did not find "+expected+ "in result set");
		return false;
	}
}
