package org.kucro3.ref;

public class RefTObject<T> {
	public RefTObject()
	{
		
	}
	
	public RefTObject(T obj)
	{
		this.ref = obj;
	}
	
	public T ref;
}
