package edu.iastate.cs510.company2.testsupport;

import edu.iastate.cs510.company2.gateway.PsGateway;

public interface MockTestControl <T extends PsGateway> {
	public void setMockInstance(T mock); // should be mockito object. 
	public T getMockInstance();
}
