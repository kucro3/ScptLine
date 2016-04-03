package org.kucro3.ref;

public class RefInt extends Ref {
	public RefInt()
	{
		super(Ref.REF_INT);
	}
	
	public RefInt(int value)
	{
		this();
		this.ref = value;
	}
	
	@Override
	public Class<?> getType()
	{
		return int.class;
	}
	
	@Override
	public Object get()
	{
		return ref;
	}
	
	@Override
	public void set(Object obj)
	{
		this.ref = (Integer)obj;
	}
	
	public int ref;
}
