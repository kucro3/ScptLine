package org.kucro3.ref;

public class RefObject extends Ref {
	public RefObject()
	{
		super(Ref.REF_OBJECT);
	}
	
	public RefObject(Object obj)
	{
		this();
		this.ref = obj;
	}
	
	@Override
	public Class<?> getType()
	{
		if(ref == null)
			return null;
		return ref.getClass();
	}
	
	@Override
	public Object get()
	{
		return ref;
	}
	
	@Override
	public void set(Object obj)
	{
		this.ref = obj;
	}
	
	public Object ref;
}
