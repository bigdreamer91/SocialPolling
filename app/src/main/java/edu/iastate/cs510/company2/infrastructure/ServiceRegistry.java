package edu.iastate.cs510.company2.infrastructure;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Robertw
 * 
 * This is meant to be a system-wide singleton which keeps track of service implementations (each also a singleton). 
 *  
 *  Usage: 
 *  
 *     // at application or test startup 
 *     ServiceRegistry registry = ServiceRegistry.getInstance();
 *     registry.register(MyInterface.class, new MyInterfaceImpl());
 *     
 *     // register other services ...
 *     
 *     // in code which needs to use a service
 *     
 *     
 *     MyInterface service = ServiceRegistry.getInstance(MyInterface.class);
 *     service.doWhatever(); 
 *     
 *   Note: if the registry is to accomplish the intended decoupling between business logic
 *   and knowledge of concrete service implementations, you MUST use only the interface (here "MyInterface") as the 
 *   registration key and as the type of the variable holding the returned reference. Otherwise you will have code
 *   that knows too much about where it is running to be testable both with Mocks and with production components. 
 *    
 *   One of the implications of this design is that at an particular time,
 *   you may only have one implementation of any registered service. 
 *
 */
public class ServiceRegistry {
	private Map<Class , PluggableService> services = new HashMap<Class,PluggableService>();
	private static ServiceRegistry thisInstance = null; 
	
	private ServiceRegistry(){
		
	}
	
	/**
	 * Adds a service implementation to the service registry. 
	 * 
	 * @param key an interface class that will be used to index svc. 
	 * @param svc an implementation of the interface referenced by key. 
	 */

	public <T> void register( Class<T> key, PluggableService svc){
		services.put(key, svc);
	}	
	/**
	 * //@param key
	 * @return the implementation (if any) registered for interface "key". 
	 *         the returned reference will be of type ifType. 
	 */	
	public <T> T getService(Class<T> ifType ){
		return (T) services.get(ifType);
	}
	
	/**
	 * public "constructor" for the ServiceReistry as a singleton. 
	 * 
	 * @return a reference to the singleton. 
	 */
	public static synchronized ServiceRegistry getInstance() {
		if (thisInstance == null ){
			thisInstance = new ServiceRegistry();			
		}
		return thisInstance;
	}
}

