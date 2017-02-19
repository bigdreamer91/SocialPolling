package edu.iastate.cs510.company2.gateway;

public enum PsStatus {
	OK(0),
	NOTOPIC(-1),
	NOKEY(-2), 
	DUPE(-3),
	NOBLOB(-4);
	
	private int stat;
	private PsStatus(int value){
		stat = value;
	}

	public int getValue(){
		return stat;
	}
}
